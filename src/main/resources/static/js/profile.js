function toggleEditForm(button, formId) {
    const form = document.getElementById(formId);
    form.classList.toggle('hidden');
    button.textContent = form.classList.contains('hidden') ? 'edit' : 'cancel';
}

function togglePasswordForm(button, formId) {
    const form = document.getElementById(formId);
    form.classList.toggle('hidden');
    button.textContent = form.classList.contains('hidden') ? 'change password' : 'cancel';
}