/**
 * User service
 * Handles user management operations
 */

import api from './api';

const USER_ENDPOINTS = {
  BASE: '/users',
  BY_ID: (id) => `/users/${id}`,
  TOGGLE_STATUS: (id) => `/users/${id}/status`,
};

/**
 * Gets all users
 * @returns {Promise} Promise containing array of users
 */
export const getAllUsers = async () => {
  try {
    const response = await api.get(USER_ENDPOINTS.BASE);
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Failed to fetch users' };
  }
};

/**
 * Gets a user by ID
 * @param {number} id - User ID
 * @returns {Promise} Promise containing user data
 */
export const getUserById = async (id) => {
  try {
    const response = await api.get(USER_ENDPOINTS.BY_ID(id));
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Failed to fetch user' };
  }
};

/**
 * Creates a new user
 * @param {Object} userData - User data
 * @returns {Promise} Promise containing created user data
 */
export const createUser = async (userData) => {
  try {
    const response = await api.post(USER_ENDPOINTS.BASE, userData);
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Failed to create user' };
  }
};

/**
 * Updates an existing user
 * @param {number} id - User ID
 * @param {Object} userData - User data
 * @returns {Promise} Promise containing updated user data
 */
export const updateUser = async (id, userData) => {
  try {
    const response = await api.put(USER_ENDPOINTS.BY_ID(id), userData);
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Failed to update user' };
  }
};

/**
 * Deletes a user
 * @param {number} id - User ID
 * @returns {Promise} Promise containing delete response
 */
export const deleteUser = async (id) => {
  try {
    const response = await api.delete(USER_ENDPOINTS.BY_ID(id));
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Failed to delete user' };
  }
};

/**
 * Toggles user active status
 * @param {number} id - User ID
 * @param {boolean} active - New active status
 * @returns {Promise} Promise containing updated user data
 */
export const toggleUserStatus = async (id, active) => {
  try {
    const response = await api.patch(USER_ENDPOINTS.TOGGLE_STATUS(id), null, {
      params: { active },
    });
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Failed to toggle user status' };
  }
};
