import React, { useEffect, useMemo, useState } from 'react'
import { CheckCircle2, Circle, FolderKanban, LayoutDashboard, LogOut, Plus, Search, Tag, X } from 'lucide-react'
import { createRoot } from 'react-dom/client'
import './styles.css'

const API = '/api'

async function api(path, options = {}) {
  const token = localStorage.getItem('accessToken')
  const response = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    }
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.message || body.error || `Yêu cầu thất bại (${response.status})`)
  }
  return response.status === 204 ? null : response.json()
}

function Auth({ onLogin }) {
  const [register, setRegister] = useState(false)
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')

  async function submit(event) {
    event.preventDefault()
    setError('')
    try {
      const result = await api(`/auth/${register ? 'register' : 'login'}`, {
        method: 'POST',
        body: JSON.stringify(form)
      })
      localStorage.setItem('accessToken', result.accessToken)
      localStorage.setItem('refreshToken', result.refreshToken)
      localStorage.setItem('user', JSON.stringify(result))
      onLogin(result)
    } catch (e) {
      setError(e.message)
    }
  }

  return <main className="auth-page"><section className="auth-card">
    <div className="brand-mark">✓</div>
    <h1>TaskFlow</h1>
    <p className="muted">Quản lý công việc đơn giản hơn</p>
    <form onSubmit={submit}>
      {register && <input required placeholder="Tên người dùng" value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} />}
      <input required type="email" placeholder="Email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} />
      <input required minLength="6" type="password" placeholder="Mật khẩu" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} />
      {error && <p className="error">{error}</p>}
      <button className="primary full">{register ? 'Tạo tài khoản' : 'Đăng nhập'}</button>
    </form>
    <button className="link-button" onClick={() => { setRegister(!register); setError('') }}>
      {register ? 'Đã có tài khoản? Đăng nhập' : 'Chưa có tài khoản? Đăng ký'}
    </button>
  </section></main>
}

function Modal({ title, onClose, children }) {
  return <div className="modal-backdrop" onMouseDown={onClose}><section className="modal" onMouseDown={e => e.stopPropagation()}>
    <header><h2>{title}</h2><button className="icon-button" onClick={onClose}><X size={18} /></button></header>
    {children}
  </section></div>
}

function App() {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || 'null'))
  const [tasks, setTasks] = useState([])
  const [projects, setProjects] = useState([])
  const [categories, setCategories] = useState([])
  const [query, setQuery] = useState('')
  const [status, setStatus] = useState('ALL')
  const [activeView, setActiveView] = useState('overview')
  const [modal, setModal] = useState(null)
  const [editing, setEditing] = useState(null)
  const [error, setError] = useState('')

  async function load() {
    try {
      const [taskData, projectData, categoryData] = await Promise.all([api('/task'), api('/project'), api('/category')])
      setTasks(taskData)
      setProjects(projectData)
      setCategories(categoryData)
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => { if (user) load() }, [user])

  const visible = useMemo(() => tasks.filter(task =>
    (status === 'ALL' || task.status === status) &&
    `${task.title} ${task.description || ''}`.toLowerCase().includes(query.toLowerCase())
  ), [tasks, status, query])

  if (!user) return <Auth onLogin={setUser} />

  async function create(event) {
    event.preventDefault()
    const data = Object.fromEntries(new FormData(event.currentTarget))
    try {
      const path = modal === 'task' ? '/task' : modal === 'project' ? '/project' : '/category'
      const body = modal === 'task'
        ? { ...data, project_id: Number(data.project_id), category_id: Number(data.category_id), user_id: user.userId }
        : modal === 'project'
          ? { name: data.name, description: data.description || '', status: data.status || 'PLANNING' }
          : { name: data.name, description: data.description || '' }
      await api(editing ? `${path}/${editing.id}` : path, {
        method: editing ? 'PUT' : 'POST',
        body: JSON.stringify(body)
      })
      setModal(null)
      setEditing(null)
      await load()
    } catch (e) {
      setError(e.message)
    }

  }

  async function remove(type, item) {
    if (!window.confirm(`Xóa ${type} "${item.name || item.title}"?`)) return
    try {
      await api(`/${type}/${item.id}`, { method: 'DELETE' })
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  function openCreate(type) {
    setEditing(null)
    setModal(type)
  }

  function openEdit(type, item) {
    setEditing(item)
    setModal(type)
  }

  async function toggleTask(task) {
    try {
      await api(`/task/${task.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          title: task.title,
          description: task.description,
          status: task.status === 'SUCCESS' ? 'TODO' : 'SUCCESS',
          priority: task.priority,
          project_id: task.projectId,
          category_id: task.categoryId,
          user_id: task.userId || user.userId
        })
      })
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  function logout() {
    localStorage.clear()
    setUser(null)
  }

  return <div className="app-shell"><aside>
    <div className="logo"><span>✓</span> TaskFlow</div>
    <nav>
      <a className={activeView === 'overview' ? 'active' : ''} href="#overview" onClick={() => setActiveView('overview')}><LayoutDashboard size={18} /> Tổng quan</a>
      <a className={activeView === 'tasks' ? 'active' : ''} href="#tasks" onClick={() => setActiveView('tasks')}><CheckCircle2 size={18} /> Công việc <b>{tasks.length}</b></a>
      <a className={activeView === 'projects' ? 'active' : ''} href="#projects" onClick={() => setActiveView('projects')}><FolderKanban size={18} /> Dự án <b>{projects.length}</b></a>
      <a className={activeView === 'categories' ? 'active' : ''} href="#categories" onClick={() => setActiveView('categories')}><Tag size={18} /> Danh mục <b>{categories.length}</b></a>
    </nav>
    <button className="logout" onClick={logout}><LogOut size={18} /> Đăng xuất</button>
  </aside>
    <main className="content"><header className="topbar"><div><p className="eyebrow">WORKSPACE</p><h1>{activeView === 'overview' ? 'Tổng quan' : activeView === 'tasks' ? 'Công việc' : activeView === 'projects' ? 'Dự án' : 'Danh mục'}</h1></div>{activeView !== 'overview' && <button className="primary" onClick={() => openCreate(activeView === 'tasks' ? 'task' : activeView === 'projects' ? 'project' : 'category')}><Plus size={18} /> Thêm mới</button>}</header>
      {error && <div className="alert">{error}<button onClick={() => setError('')}><X size={16} /></button></div>}
      {activeView === 'overview' && <><section className="stats" id="overview"><div><span>Tổng công việc</span><strong>{tasks.length}</strong><small>Đang theo dõi</small></div><div><span>Hoàn thành</span><strong>{tasks.filter(t => t.status === 'SUCCESS').length}</strong><small className="success">Đã hoàn tất</small></div><div><span>Đang thực hiện</span><strong>{tasks.filter(t => t.status === 'TODO').length}</strong><small>Chờ xử lý</small></div><div><span>Dự án</span><strong>{projects.length}</strong><small>Đang quản lý</small></div></section><section className="panel"><div className="panel-head"><div><h2>Công việc gần đây</h2><p className="muted">Chọn Công việc trong menu để quản lý đầy đủ</p></div><button className="secondary" onClick={() => setActiveView('tasks')}>Xem tất cả</button></div><div className="task-list">{tasks.slice(0, 5).map(task => <article className="task-row" key={task.id}><div className="task-main"><h3>{task.title}</h3><p>{task.description || 'Không có mô tả'}</p></div><span className={`badge ${task.status === 'SUCCESS' ? 'green' : 'orange'}`}>{task.status === 'SUCCESS' ? 'Hoàn thành' : 'Chờ xử lý'}</span></article>)}{!tasks.length && <div className="empty">Chưa có công việc.</div>}</div></section></>}
      {activeView === 'tasks' && <section className="panel" id="tasks"><div className="panel-head"><div><h2>Tất cả công việc</h2><p className="muted">Theo dõi và cập nhật công việc</p></div><div className="panel-actions"><button className="secondary" onClick={() => openCreate('category')}><Plus size={16} /> Danh mục</button><button className="secondary" onClick={() => openCreate('project')}><Plus size={16} /> Dự án mới</button></div></div>
        <div className="toolbar"><div className="search"><Search size={17} /><input placeholder="Tìm công việc..." value={query} onChange={e => setQuery(e.target.value)} /></div><select value={status} onChange={e => setStatus(e.target.value)}><option value="ALL">Tất cả trạng thái</option><option value="TODO">Chờ xử lý</option><option value="SUCCESS">Hoàn thành</option></select></div>
        <div className="task-list">{visible.map(task => <article className="task-row" key={task.id}><button aria-label="Cập nhật trạng thái" className={`check ${task.status === 'SUCCESS' ? 'done' : ''}`} onClick={() => toggleTask(task)}>{task.status === 'SUCCESS' ? <CheckCircle2 size={20} /> : <Circle size={20} />}</button><div className="task-main"><h3>{task.title}</h3><p>{task.description || 'Không có mô tả'} · {task.projectName || 'Chưa có dự án'} · {task.categoryName || 'Chưa có danh mục'}</p></div><span className={`badge ${task.status === 'SUCCESS' ? 'green' : 'orange'}`}>{task.status === 'SUCCESS' ? 'Hoàn thành' : 'Chờ xử lý'}</span><span className={`priority ${task.priority.toLowerCase()}`}>{task.priority}</span><button className="row-action" onClick={() => openEdit('task', task)}>Sửa</button><button className="row-action danger" onClick={() => remove('task', task)}>Xóa</button></article>)}{!visible.length && <div className="empty">Chưa có công việc phù hợp.</div>}</div>
      </section>}
      {activeView === 'projects' && <section className="panel" id="projects"><div className="section-title"><div><h2>Tất cả dự án</h2><p className="muted">Quản lý toàn bộ dự án</p></div></div><div className="entity-grid">{projects.map(project => <article className="entity-card" key={project.id}><div><h3>{project.name}</h3><p>{project.description || 'Không có mô tả'}</p><small>{project.status} · {project.tasks?.length || 0} công việc</small></div><div className="card-actions"><button onClick={() => openEdit('project', project)}>Sửa</button><button className="danger" onClick={() => remove('project', project)}>Xóa</button></div></article>)}{!projects.length && <div className="empty">Chưa có dự án.</div>}</div></section>}
      {activeView === 'categories' && <section className="panel" id="categories"><div className="section-title"><div><h2>Tất cả danh mục</h2><p className="muted">Phân loại công việc</p></div></div><div className="entity-grid">{categories.map(category => <article className="entity-card" key={category.id}><div><h3>{category.name}</h3><p>{category.description || 'Không có mô tả'}</p></div><div className="card-actions"><button onClick={() => openEdit('category', category)}>Sửa</button><button className="danger" onClick={() => remove('category', category)}>Xóa</button></div></article>)}{!categories.length && <div className="empty">Chưa có danh mục.</div>}</div></section>}
    </main>
    {modal && <Modal title={`${editing ? 'Sửa' : 'Tạo'} ${modal === 'task' ? 'công việc' : modal === 'project' ? 'dự án' : 'danh mục'}`} onClose={() => { setModal(null); setEditing(null) }}><form className="modal-form" onSubmit={create}>    {modal === 'task' ? <><input name="title" required placeholder="Tên công việc" defaultValue={editing?.title || ''} /><textarea name="description" placeholder="Mô tả" defaultValue={editing?.description || ''} /><select name="project_id" required defaultValue={editing?.projectId || ''}><option value="">Chọn dự án</option>{projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}</select><select name="category_id" required defaultValue={editing?.categoryId || ''}><option value="">Chọn danh mục</option>{categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select><select name="priority" defaultValue={editing?.priority || 'MEDIUM'}><option>LOW</option><option>MEDIUM</option><option>HIGH</option></select><select name="status" defaultValue={editing?.status || 'TODO'}><option value="TODO">Chờ xử lý</option><option value="SUCCESS">Hoàn thành</option></select></> : <><input name="name" required placeholder={modal === 'project' ? 'Tên dự án' : 'Tên danh mục'} defaultValue={editing?.name || ''} /><textarea name="description" placeholder="Mô tả" defaultValue={editing?.description || ''} />{modal === 'project' && <select name="status" defaultValue={editing?.status || 'PLANNING'}><option>PLANNING</option><option>SUCCESS</option></select>}</>}<button className="primary full">Lưu lại</button></form></Modal>}
  </div>
}

createRoot(document.getElementById('root')).render(<App />)
