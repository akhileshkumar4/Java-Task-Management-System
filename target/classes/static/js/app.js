/**
 * TodoList Application JavaScript
 * Provides interactive functionality for the TodoList web application
 */

// Application initialization
document.addEventListener('DOMContentLoaded', function() {
    console.log('TodoList Application initialized');
    initializeApp();
});

// Initialize application components
function initializeApp() {
    initializeTooltips();
    initializeAlerts();
    initializeModals();
    initializeFormValidation();
    initializeKeyboardShortcuts();
    initializeToggleAnimations();
    initializeSearchFeatures();
    updateTimestamps();

    // Update timestamps every minute
    setInterval(updateTimestamps, 60000);
}

// Initialize Bootstrap tooltips
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Auto-dismiss alerts after 5 seconds
function initializeAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

// Initialize modal handling
function initializeModals() {
    const addTaskModal = document.getElementById('addTaskModal');
    if (addTaskModal) {
        addTaskModal.addEventListener('show.bs.modal', function (event) {
            // Clear form when modal opens
            const form = addTaskModal.querySelector('form');
            if (form) {
                form.reset();
            }

            // Focus on title field
            const titleField = addTaskModal.querySelector('#title');
            if (titleField) {
                setTimeout(() => titleField.focus(), 100);
            }
        });

        addTaskModal.addEventListener('hidden.bs.modal', function (event) {
            // Clear any validation errors when modal closes
            clearFormErrors(addTaskModal);
        });
    }
}

// Initialize form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();

                // Focus on first invalid field
                const firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                }
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Real-time validation
    const inputs = document.querySelectorAll('input, textarea, select');
    inputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            validateField(input);
        });

        input.addEventListener('input', function() {
            if (input.classList.contains('is-invalid')) {
                validateField(input);
            }
        });
    });
}

// Initialize keyboard shortcuts
function initializeKeyboardShortcuts() {
    document.addEventListener('keydown', function(event) {
        // Ctrl+N or Cmd+N: New task
        if ((event.ctrlKey || event.metaKey) && event.key === 'n') {
            event.preventDefault();
            const addTaskModal = document.getElementById('addTaskModal');
            if (addTaskModal) {
                new bootstrap.Modal(addTaskModal).show();
            }
        }

        // Escape: Close modals
        if (event.key === 'Escape') {
            const openModal = document.querySelector('.modal.show');
            if (openModal) {
                const modal = bootstrap.Modal.getInstance(openModal);
                if (modal) {
                    modal.hide();
                }
            }
        }

        // Ctrl+F or Cmd+F: Focus search
        if ((event.ctrlKey || event.metaKey) && event.key === 'f') {
            const searchInput = document.querySelector('input[name="search"]');
            if (searchInput) {
                event.preventDefault();
                searchInput.focus();
                searchInput.select();
            }
        }
    });
}

// Initialize toggle animations
function initializeToggleAnimations() {
    const toggleButtons = document.querySelectorAll('.toggle-btn');
    toggleButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            // Add loading state
            button.classList.add('loading');

            // Remove loading state after a short delay (will be removed by page reload)
            setTimeout(() => {
                button.classList.remove('loading');
            }, 1000);
        });
    });
}

// Initialize search features
function initializeSearchFeatures() {
    const searchInput = document.querySelector('input[name="search"]');
    if (searchInput) {
        // Add clear button functionality
        const clearButton = document.createElement('button');
        clearButton.type = 'button';
        clearButton.className = 'btn btn-outline-secondary';
        clearButton.innerHTML = '<i class="bi bi-x"></i>';
        clearButton.title = 'Clear search';

        clearButton.addEventListener('click', function() {
            searchInput.value = '';
            searchInput.form.submit();
        });

        // Show clear button when there's text
        searchInput.addEventListener('input', function() {
            const hasValue = searchInput.value.trim().length > 0;
            clearButton.style.display = hasValue ? 'block' : 'none';
        });
    }
}

// Update relative timestamps
function updateTimestamps() {
    const timestamps = document.querySelectorAll('[data-timestamp]');
    timestamps.forEach(function(element) {
        const timestamp = element.getAttribute('data-timestamp');
        if (timestamp) {
            element.textContent = formatRelativeTime(new Date(timestamp));
        }
    });
}

// Utility Functions

// Validate individual form field
function validateField(field) {
    if (field.checkValidity()) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        hideFieldError(field);
    } else {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        showFieldError(field, field.validationMessage);
    }
}

// Show field error
function showFieldError(field, message) {
    let errorElement = field.parentNode.querySelector('.field-error');
    if (!errorElement) {
        errorElement = document.createElement('div');
        errorElement.className = 'field-error text-danger small mt-1';
        field.parentNode.appendChild(errorElement);
    }
    errorElement.textContent = message;
}

// Hide field error
function hideFieldError(field) {
    const errorElement = field.parentNode.querySelector('.field-error');
    if (errorElement) {
        errorElement.remove();
    }
}

// Clear form errors
function clearFormErrors(container) {
    const errorElements = container.querySelectorAll('.field-error');
    errorElements.forEach(function(element) {
        element.remove();
    });

    const invalidFields = container.querySelectorAll('.is-invalid, .is-valid');
    invalidFields.forEach(function(field) {
        field.classList.remove('is-invalid', 'is-valid');
    });
}

// Format relative time
function formatRelativeTime(date) {
    const now = new Date();
    const diff = now - date;
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) {
        return days === 1 ? '1 day ago' : `${days} days ago`;
    } else if (hours > 0) {
        return hours === 1 ? '1 hour ago' : `${hours} hours ago`;
    } else if (minutes > 0) {
        return minutes === 1 ? '1 minute ago' : `${minutes} minutes ago`;
    } else {
        return 'Just now';
    }
}

// Show notification
function showNotification(message, type = 'info', duration = 3000) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(alertDiv);

    // Auto-remove after duration
    setTimeout(() => {
        if (alertDiv.parentNode) {
            new bootstrap.Alert(alertDiv).close();
        }
    }, duration);
}

// Confirm action
function confirmAction(message, callback) {
    if (confirm(message)) {
        if (typeof callback === 'function') {
            callback();
        }
        return true;
    }
    return false;
}

// Export functions for global access
window.TodoApp = {
    showNotification: showNotification,
    confirmAction: confirmAction,
    updateTimestamps: updateTimestamps
};

// Task-specific functions
function toggleTask(taskId) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = `/tasks/${taskId}/toggle`;
    document.body.appendChild(form);
    form.submit();
}

function deleteTask(taskId, taskTitle) {
    if (confirmAction(`Are you sure you want to delete "${taskTitle}"?`)) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/tasks/${taskId}/delete`;
        document.body.appendChild(form);
        form.submit();
    }
}

function editTask(taskId) {
    window.location.href = `/tasks/${taskId}/edit`;
}

// Global function assignments for HTML onclick handlers
window.toggleTask = toggleTask;
window.deleteTask = deleteTask;
window.editTask = editTask;

// Statistics update (if needed for real-time updates)
function updateStats() {
    fetch('/api/tasks/stats')
        .then(response => response.json())
        .then(data => {
            // Update stat cards if they exist
            const totalElement = document.querySelector('[data-stat="total"]');
            const completedElement = document.querySelector('[data-stat="completed"]');
            const pendingElement = document.querySelector('[data-stat="pending"]');
            const overdueElement = document.querySelector('[data-stat="overdue"]');

            if (totalElement) totalElement.textContent = data.total;
            if (completedElement) completedElement.textContent = data.completed;
            if (pendingElement) pendingElement.textContent = data.pending;
            if (overdueElement) overdueElement.textContent = data.overdue;
        })
        .catch(error => console.error('Error updating stats:', error));
}

// Auto-save functionality (if implemented)
function autoSave(formData) {
    // Implementation for auto-saving drafts
    localStorage.setItem('todolist_draft', JSON.stringify(formData));
}

function loadDraft() {
    // Implementation for loading drafts
    const draft = localStorage.getItem('todolist_draft');
    if (draft) {
        return JSON.parse(draft);
    }
    return null;
}

function clearDraft() {
    localStorage.removeItem('todolist_draft');
}

console.log('TodoList Application JavaScript loaded successfully');