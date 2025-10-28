/**
 * Authentication service
 * Handles user authentication operations
 */

import api from './api';

const AUTH_ENDPOINTS = {
  LOGIN: '/auth/login',
  LOGOUT: '/auth/logout',
};

/**
 * Authenticates a user with username and password
 * @param {string} username - User's username
 * @param {string} password - User's password
 * @returns {Promise} Promise containing authentication response
 */
export const login = async (username, password) => {
  try {
    const response = await api.post(AUTH_ENDPOINTS.LOGIN, {
      username,
      password,
    });
    
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Login failed' };
  }
};

/**
 * Logs out the current user
 * @returns {Promise} Promise containing logout response
 */
export const logout = async () => {
  try {
    await api.post(AUTH_ENDPOINTS.LOGOUT);
  } finally {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
};

/**
 * Gets the current user from local storage
 * @returns {Object|null} Current user object or null
 */
export const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

/**
 * Checks if user is authenticated
 * @returns {boolean} True if user is authenticated
 */
export const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};

/**
 * Checks if user has a specific role
 * @param {string} role - Role to check
 * @returns {boolean} True if user has the role
 */
export const hasRole = (role) => {
  const user = getCurrentUser();
  return user?.role === role;
};
