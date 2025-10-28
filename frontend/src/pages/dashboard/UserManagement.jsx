/**
 * User Management Page
 * Displays and manages all users in the system
 */

import { useState, useEffect } from 'react';
import * as userService from '../../services/userService';
import UserFormModal from '../../components/users/UserFormModal';
import '../../styles/table.css';
import '../../styles/button.css';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await userService.getAllUsers();
      setUsers(data);
      setError('');
    } catch (err) {
      setError(err.message || 'Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setSelectedUser(null);
    setIsModalOpen(true);
  };

  const handleEdit = (user) => {
    setSelectedUser(user);
    setIsModalOpen(true);
  };

  const handleSubmit = async (formData) => {
    try {
      setActionLoading(true);
      if (selectedUser) {
        await userService.updateUser(selectedUser.id, formData);
      } else {
        await userService.createUser(formData);
      }
      setIsModalOpen(false);
      fetchUsers();
    } catch (err) {
      alert(err.message || 'Operation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleToggleStatus = async (user) => {
    if (globalThis.confirm(`Are you sure you want to ${user.active ? 'deactivate' : 'activate'} this user?`)) {
      try {
        await userService.toggleUserStatus(user.id, !user.active);
        fetchUsers();
      } catch (err) {
        alert(err.message || 'Failed to toggle user status');
      }
    }
  };

  const handleDelete = async (user) => {
    if (globalThis.confirm(`Are you sure you want to delete ${user.username}? This action cannot be undone.`)) {
      try {
        await userService.deleteUser(user.id);
        fetchUsers();
      } catch (err) {
        alert(err.message || 'Failed to delete user');
      }
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 'var(--spacing-3xl)' }}>
        <p>Loading users...</p>
      </div>
    );
  }

  return (
    <div>
      <div className="table-container">
        <div className="table-header">
          <h2 className="table-title">User Management</h2>
          <div className="table-actions">
            <button className="btn btn-primary" onClick={handleCreate}>
              + Create User
            </button>
          </div>
        </div>

        {error && (
          <div
            style={{
              padding: 'var(--spacing-md)',
              margin: 'var(--spacing-lg)',
              backgroundColor: 'var(--color-error-light)',
              color: 'var(--color-error)',
              borderRadius: 'var(--radius-md)',
            }}
          >
            {error}
          </div>
        )}

        <div className="table-wrapper">
          <table className="table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: 'var(--spacing-xl)' }}>
                    No users found
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.username}</td>
                    <td>{`${user.firstName} ${user.lastName}`}</td>
                    <td>{user.email}</td>
                    <td>{user.phoneNumber || '-'}</td>
                    <td>
                      <span className={`badge ${user.role === 'ADMIN' ? 'badge-info' : 'badge-success'}`}>
                        {user.role.replace('_', ' ')}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${user.active ? 'badge-success' : 'badge-error'}`}>
                        {user.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <div className="table-actions-cell">
                        <button
                          className="btn btn-sm btn-outline"
                          onClick={() => handleEdit(user)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-sm btn-outline"
                          onClick={() => handleToggleStatus(user)}
                        >
                          {user.active ? 'Deactivate' : 'Activate'}
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(user)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <UserFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleSubmit}
        user={selectedUser}
        loading={actionLoading}
      />
    </div>
  );
};

export default UserManagement;
