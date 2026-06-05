const API = "http://localhost:8080/api";
const STATUSES = ["UNDER_REVIEW", "SELECTED", "WAITING_LIST", "REJECTED"];

function App() {
  const [token, setToken] = React.useState(localStorage.getItem("resumeiq_token") || "");
  const [user, setUser] = React.useState(JSON.parse(localStorage.getItem("resumeiq_user") || "null"));
  const [view, setView] = React.useState("dashboard");
  const [jobs, setJobs] = React.useState([]);
  const [apps, setApps] = React.useState([]);
  const [analytics, setAnalytics] = React.useState(null);
  const [selectedJob, setSelectedJob] = React.useState("");
  const [sort, setSort] = React.useState("best");
  const [skillFilter, setSkillFilter] = React.useState("");
  const [message, setMessage] = React.useState("");

  const api = React.useCallback(async (path, options = {}) => {
    const headers = { ...(options.headers || {}) };
    if (token) headers.Authorization = `Bearer ${token}`;
    if (!(options.body instanceof FormData)) headers["Content-Type"] = "application/json";
    const response = await fetch(`${API}${path}`, { ...options, headers });
    if (!response.ok) {
      const body = await response.json().catch(() => ({ message: "Request failed" }));
      throw new Error(body.message || "Request failed");
    }
    const type = response.headers.get("content-type") || "";
    return type.includes("application/json") ? response.json() : response.text();
  }, [token]);

  const refresh = React.useCallback(async () => {
    if (!token) return;
    const [jobData, appData, analyticsData] = await Promise.all([
      api("/jobs"),
      api(`/applications?sort=${sort}&skills=${encodeURIComponent(skillFilter)}${selectedJob ? `&jobRoleId=${selectedJob}` : ""}`),
      api("/analytics"),
    ]);
    setJobs(jobData);
    setApps(appData);
    setAnalytics(analyticsData);
  }, [api, token, sort, skillFilter, selectedJob]);

  React.useEffect(() => {
    refresh().catch(error => setMessage(error.message));
  }, [refresh]);

  async function login(email, password) {
    const data = await api("/auth/login", { method: "POST", body: JSON.stringify({ email, password }) });
    setToken(data.token);
    setUser(data);
    localStorage.setItem("resumeiq_token", data.token);
    localStorage.setItem("resumeiq_user", JSON.stringify(data));
  }

  function logout() {
    localStorage.clear();
    setToken("");
    setUser(null);
  }

  if (!token) return <Login onLogin={login} message={message} setMessage={setMessage} />;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">ResumeIQ</div>
        {["dashboard", "jobs", "upload", "applicants", "suggested", "reports"].map(item => (
          <button key={item} className={`nav-button ${view === item ? "active" : ""}`} onClick={() => setView(item)}>
            {title(item)}
          </button>
        ))}
      </aside>
      <main className="content">
        <div className="topbar">
          <div>
            <h1 className="h3 mb-1">{title(view)}</h1>
            <div className="text-secondary">{user?.name} · {user?.role}</div>
          </div>
          <button className="btn btn-outline-secondary" onClick={logout}>Logout</button>
        </div>
        {message && <div className="alert alert-info" onClick={() => setMessage("")}>{message}</div>}
        {view === "dashboard" && <Dashboard analytics={analytics} />}
        {view === "jobs" && <Jobs jobs={jobs} api={api} refresh={refresh} setMessage={setMessage} />}
        {view === "upload" && <Upload jobs={jobs} api={api} refresh={refresh} setMessage={setMessage} />}
        {view === "applicants" && <Applicants apps={apps} jobs={jobs} api={api} refresh={refresh} selectedJob={selectedJob} setSelectedJob={setSelectedJob} sort={sort} setSort={setSort} skillFilter={skillFilter} setSkillFilter={setSkillFilter} />}
        {view === "suggested" && <Suggested jobs={jobs} api={api} setMessage={setMessage} />}
        {view === "reports" && <Reports token={token} />}
      </main>
    </div>
  );
}

function Login({ onLogin, message, setMessage }) {
  const [email, setEmail] = React.useState("admin@resumeiq.local");
  const [password, setPassword] = React.useState("admin123");
  async function submit(event) {
    event.preventDefault();
    try {
      await onLogin(email, password);
    } catch (error) {
      setMessage(error.message);
    }
  }
  return (
    <div className="login-page">
      <div className="panel login-card">
        <h1 className="h3">ResumeIQ</h1>
        <p className="text-secondary">Recruiter intelligence console</p>
        {message && <div className="alert alert-danger">{message}</div>}
        <form onSubmit={submit}>
          <label className="form-label">Email</label>
          <input className="form-control mb-3" value={email} onChange={e => setEmail(e.target.value)} />
          <label className="form-label">Password</label>
          <input className="form-control mb-3" type="password" value={password} onChange={e => setPassword(e.target.value)} />
          <button className="btn btn-primary w-100">Login</button>
        </form>
      </div>
    </div>
  );
}

function Dashboard({ analytics }) {
  const counts = analytics?.statusCounts || {};
  return (
    <>
      <div className="row g-3 mb-3">
        {[
          ["Jobs", analytics?.jobs || 0],
          ["Candidates", analytics?.candidates || 0],
          ["Applications", analytics?.applications || 0],
          ["Avg Match", `${analytics?.averageMatchScore || 0}%`],
        ].map(([label, value]) => (
          <div className="col-md-3" key={label}><div className="panel metric"><div className="text-secondary">{label}</div><div className="h3">{value}</div></div></div>
        ))}
      </div>
      <div className="row g-3">
        <div className="col-lg-6"><div className="panel"><h2 className="h5">Workflow</h2>{STATUSES.map(s => <div key={s} className="d-flex justify-content-between border-bottom py-2"><span>{readable(s)}</span><strong>{counts[s] || 0}</strong></div>)}</div></div>
        <div className="col-lg-6"><div className="panel"><h2 className="h5">Top Skills</h2>{Object.entries(analytics?.topSkills || {}).slice(0, 12).map(([skill, count]) => <span className="chip" key={skill}>{skill} · {count}</span>)}</div></div>
      </div>
    </>
  );
}

function Jobs({ jobs, api, refresh, setMessage }) {
  const empty = { title: "", department: "", minExperienceYears: 0, requiredEducation: "Any", requiredSkills: "", preferredSkills: "", description: "" };
  const [form, setForm] = React.useState(empty);
  async function submit(event) {
    event.preventDefault();
    await api("/jobs", { method: "POST", body: JSON.stringify({ ...form, requiredSkills: split(form.requiredSkills), preferredSkills: split(form.preferredSkills), active: true }) });
    setForm(empty);
    setMessage("Job role saved");
    refresh();
  }
  return (
    <div className="row g-3">
      <div className="col-lg-5">
        <form className="panel" onSubmit={submit}>
          <h2 className="h5">Create Job Role</h2>
          {["title", "department", "requiredEducation"].map(field => <input key={field} className="form-control mb-2" placeholder={title(field)} value={form[field]} onChange={e => setForm({ ...form, [field]: e.target.value })} />)}
          <input className="form-control mb-2" type="number" placeholder="Minimum experience" value={form.minExperienceYears} onChange={e => setForm({ ...form, minExperienceYears: Number(e.target.value) })} />
          <input className="form-control mb-2" placeholder="Required skills, comma separated" value={form.requiredSkills} onChange={e => setForm({ ...form, requiredSkills: e.target.value })} />
          <input className="form-control mb-2" placeholder="Preferred skills, comma separated" value={form.preferredSkills} onChange={e => setForm({ ...form, preferredSkills: e.target.value })} />
          <textarea className="form-control mb-3" placeholder="Description" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
          <button className="btn btn-primary">Save Role</button>
        </form>
      </div>
      <div className="col-lg-7">
        <div className="panel">
          <h2 className="h5">Open Roles</h2>
          {jobs.map(job => <div className="border-bottom py-3" key={job.id}><div className="fw-bold">{job.title}</div><div className="text-secondary">{job.department} · {job.minExperienceYears} yrs · {job.requiredEducation}</div><div>{job.requiredSkills?.map(skill => <span className="chip" key={skill}>{skill}</span>)}</div></div>)}
        </div>
      </div>
    </div>
  );
}

function Upload({ jobs, api, refresh, setMessage }) {
  const [jobRoleId, setJobRoleId] = React.useState("");
  const [file, setFile] = React.useState(null);
  async function submit(event) {
    event.preventDefault();
    const body = new FormData();
    body.append("jobRoleId", jobRoleId);
    body.append("file", file);
    const app = await api("/applications/upload", { method: "POST", body });
    setMessage(`Resume analyzed for ${app.candidate.name} with ${app.matchScore}% match`);
    refresh();
  }
  return (
    <form className="panel" onSubmit={submit}>
      <h2 className="h5">Upload Resume</h2>
      <select className="form-select mb-3" value={jobRoleId} onChange={e => setJobRoleId(e.target.value)} required>
        <option value="">Choose job role</option>
        {jobs.map(job => <option value={job.id} key={job.id}>{job.title}</option>)}
      </select>
      <input className="form-control mb-3" type="file" accept=".pdf,.docx" onChange={e => setFile(e.target.files[0])} required />
      <button className="btn btn-primary">Analyze Resume</button>
    </form>
  );
}

function Applicants({ apps, jobs, api, refresh, selectedJob, setSelectedJob, sort, setSort, skillFilter, setSkillFilter }) {
  async function status(id, value) {
    await api(`/applications/${id}/status`, { method: "PATCH", body: JSON.stringify({ status: value }) });
    refresh();
  }
  async function reanalyze(id) {
    await api(`/applications/${id}/reanalyze`, { method: "POST" });
    refresh();
  }
  return (
    <div className="panel">
      <div className="row g-2 mb-3">
        <div className="col-md-3"><select className="form-select" value={selectedJob} onChange={e => setSelectedJob(e.target.value)}><option value="">All roles</option>{jobs.map(job => <option key={job.id} value={job.id}>{job.title}</option>)}</select></div>
        <div className="col-md-3"><select className="form-select" value={sort} onChange={e => setSort(e.target.value)}><option value="best">Best Match</option><option value="least">Least Match</option><option value="experience">Experience</option><option value="education">Education</option></select></div>
        <div className="col-md-4"><input className="form-control" placeholder="Custom skills: Java, React" value={skillFilter} onChange={e => setSkillFilter(e.target.value)} /></div>
      </div>
      <div className="table-responsive">
        <table className="table">
          <thead><tr><th>Candidate</th><th>Role</th><th>Score</th><th>Skills</th><th>Gaps</th><th>Status</th><th></th></tr></thead>
          <tbody>{apps.map(app => <tr key={app.id}>
            <td><strong>{app.candidate.name}</strong><div className="text-secondary small">{app.candidate.email}</div><div className="small">{app.candidate.experienceYears} yrs · {app.candidate.highestEducation}</div></td>
            <td>{app.jobRole.title}<div className="small text-secondary">Suggested: {app.suggestedRole}</div></td>
            <td><span className="score">{app.matchScore}%</span><div className="small">ATS {app.candidate.atsScore}%</div></td>
            <td>{app.matchedSkills?.map(skill => <span className="chip" key={skill}>{skill}</span>)}</td>
            <td>{app.missingSkills?.map(skill => <span className="chip missing" key={skill}>{skill}</span>)}</td>
            <td><select className="form-select status-select" value={app.status} onChange={e => status(app.id, e.target.value)}>{STATUSES.map(s => <option key={s}>{s}</option>)}</select></td>
            <td><button className="btn btn-sm btn-outline-primary" onClick={() => reanalyze(app.id)}>Re-analyze</button></td>
          </tr>)}</tbody>
        </table>
      </div>
    </div>
  );
}

function Suggested({ jobs, api, setMessage }) {
  const [jobId, setJobId] = React.useState("");
  const [items, setItems] = React.useState([]);
  async function load(value) {
    setJobId(value);
    if (!value) return setItems([]);
    const data = await api(`/applications/suggested/${value}`);
    setItems(data);
    setMessage(`${data.length} suggested applicants found`);
  }
  return (
    <div className="panel">
      <select className="form-select mb-3" value={jobId} onChange={e => load(e.target.value)}>
        <option value="">Choose role</option>
        {jobs.map(job => <option key={job.id} value={job.id}>{job.title}</option>)}
      </select>
      {items.map(app => <div className="border-bottom py-3" key={`${app.candidate.id}-${app.jobRole.id}`}><strong>{app.candidate.name}</strong> <span className="score ms-2">{app.matchScore}%</span><div className="text-secondary">{app.candidate.skills?.join(", ")}</div><div>{app.recommendations}</div></div>)}
    </div>
  );
}

function Reports({ token }) {
  const url = `${API}/reports/hiring.csv`;
  return <div className="panel"><h2 className="h5">Hiring Reports</h2><a className="btn btn-primary" href={url} onClick={e => { e.preventDefault(); fetch(url, { headers: { Authorization: `Bearer ${token}` } }).then(r => r.blob()).then(blob => { const a = document.createElement("a"); a.href = URL.createObjectURL(blob); a.download = "resumeiq-hiring-report.csv"; a.click(); }); }}>Download CSV Report</a></div>;
}

function split(value) {
  return value.split(",").map(item => item.trim()).filter(Boolean);
}

function title(value) {
  return value.replace(/([A-Z])/g, " $1").replace(/^./, c => c.toUpperCase());
}

function readable(value) {
  return value.replaceAll("_", " ").toLowerCase().replace(/^./, c => c.toUpperCase());
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
