let annotatorToDelete = null;
const deleteConfirmModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
const addAnnotatorModal = new bootstrap.Modal(document.getElementById('addAnnotatorModal'));

// Initialize form submission
document.getElementById('addAnnotatorForm').addEventListener('submit', function(e) {
    e.preventDefault();
    saveAnnotator();
});

// Save new annotator
function saveAnnotator() {
    const form = document.getElementById('addAnnotatorForm');
    const saveButton = document.getElementById('saveAnnotatorBtn');
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    // Validate form data
    if (!validateFormData(data)) {
        return;
    }

    // Show loading state
    const originalButtonText = saveButton.innerHTML;
    saveButton.disabled = true;
    saveButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Saving...';

    fetch('/api/annotators', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                if (response.status === 409) {
                    throw new Error('Email already exists');
                }
                // Handle validation errors
                if (response.status === 400) {
                    const errorMessages = Object.entries(err)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join('\n');
                    throw new Error(errorMessages);
                }
                throw new Error(err.message || 'Error creating annotator');
            });
        }
        return response.json();
    })
    .then(data => {
        // Show success message
        showNotification('Annotator added successfully!', 'success');
        // Reset form and close modal
        form.reset();
        addAnnotatorModal.hide();
        // Reload the page after a short delay
        setTimeout(() => window.location.reload(), 1500);
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification(error.message, 'error');
    })
    .finally(() => {
        // Reset button state
        saveButton.disabled = false;
        saveButton.innerHTML = originalButtonText;
    });
}

// Validate form data
function validateFormData(data) {
    const errors = [];

    if (!data.prenom || data.prenom.trim().length < 2) {
        errors.push('First name must be at least 2 characters long');
    }

    if (!data.nom || data.nom.trim().length < 2) {
        errors.push('Last name must be at least 2 characters long');
    }

    if (!data.email || !isValidEmail(data.email)) {
        errors.push('Please enter a valid email address');
    }

    if (!data.password || data.password.length < 6) {
        errors.push('Password must be at least 6 characters long');
    }

    // Password complexity validation
    const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{6,}$/;
    if (data.password && !passwordRegex.test(data.password)) {
        errors.push('Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character');
    }

    if (errors.length > 0) {
        showNotification(errors.join('<br>'), 'error');
        return false;
    }

    return true;
}

// Validate email format
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Show notification
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
    notification.style.zIndex = '9999';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    // Add to document
    document.body.appendChild(notification);

    // Auto remove after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

// Edit annotator
function editAnnotator(id) {
    fetch(`/api/annotators/${id}`)
        .then(response => response.json())
        .then(data => {
            const form = document.getElementById('editAnnotatorForm');
            form.idUser.value = data.idUser;
            form.prenom.value = data.prenom;
            form.nom.value = data.nom;
            form.email.value = data.email;
            form.password.value = data.password;
            
            new bootstrap.Modal(document.getElementById('editAnnotatorModal')).show();
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error loading annotator data', 'error');
        });
}

// Update annotator
function updateAnnotator() {
    const form = document.getElementById('editAnnotatorForm');
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    const id = data.idUser;

    fetch(`/api/annotators/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            showNotification('Annotator updated successfully!', 'success');
            setTimeout(() => window.location.reload(), 1500);
        } else {
            throw new Error('Error updating annotator');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification(error.message, 'error');
    });
}

// Delete annotator
function deleteAnnotator(id) {
    annotatorToDelete = id;
    deleteConfirmModal.show();
}

// Handle delete confirmation
document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
    if (annotatorToDelete) {
        fetch(`/api/annotators/${annotatorToDelete}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                deleteConfirmModal.hide();
                showNotification('Annotator deleted successfully!', 'success');
                setTimeout(() => window.location.reload(), 1500);
            } else {
                throw new Error('Error deleting annotator');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification(error.message, 'error');
        });
    }
}); 