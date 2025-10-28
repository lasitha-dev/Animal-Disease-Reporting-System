/**
 * Dashboard Layout Component
 * Main layout for authenticated users
 */

import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import '../../styles/dashboard.css';
import '../../styles/button.css';

const DashboardLayout = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const isActive = (path) => {
    return location.pathname === path ? 'active' : '';
  };

  const getInitials = () => {
    if (!user) return '';
    return `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase();
  };

  return (
    <div className="dashboard">
      <aside className="dashboard-sidebar">
        <div className="dashboard-header">
          <div className="dashboard-logo">
            <span>🐄</span>
            <span>ADRS</span>
          </div>
        </div>

        <nav className="dashboard-nav">
          <ul>
            <li>
              <Link to="/dashboard" className={isActive('/dashboard')}>
                <span>📊</span>
                <span>Dashboard</span>
              </Link>
            </li>
            {isAdmin && (
              <li>
                <Link to="/dashboard/users" className={isActive('/dashboard/users')}>
                  <span>👥</span>
                  <span>User Management</span>
                </Link>
              </li>
            )}
            <li>
              <Link to="/dashboard/farms" className={isActive('/dashboard/farms')}>
                <span>🏡</span>
                <span>Farms</span>
              </Link>
            </li>
            <li>
              <Link to="/dashboard/animals" className={isActive('/dashboard/animals')}>
                <span>🐮</span>
                <span>Animals</span>
              </Link>
            </li>
            <li>
              <Link to="/dashboard/diseases" className={isActive('/dashboard/diseases')}>
                <span>🦠</span>
                <span>Diseases</span>
              </Link>
            </li>
          </ul>
        </nav>

        <div className="dashboard-user">
          <div className="dashboard-user-info">
            <div className="dashboard-user-avatar">{getInitials()}</div>
            <div className="dashboard-user-details">
              <div className="dashboard-user-name">
                {user?.firstName} {user?.lastName}
              </div>
              <div className="dashboard-user-role">
                {user?.role?.replace('_', ' ')}
              </div>
            </div>
          </div>
          <button className="btn btn-outline btn-block btn-sm" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </aside>

      <main className="dashboard-main">
        <div className="dashboard-topbar">
          <h1 style={{ fontSize: 'var(--font-size-xl)', fontWeight: 'var(--font-weight-semibold)' }}>
            Animal Disease Reporting System
          </h1>
        </div>

        <div className="dashboard-content">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default DashboardLayout;
