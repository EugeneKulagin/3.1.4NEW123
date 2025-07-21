// === URL API ===
const API_URL = '/api/admin';

// === ССЫЛКИ НА DOM-ЭЛЕМЕНТЫ ===
const usersTableBody = document.getElementById('usersTableBody');
const dynamicModal = document.getElementById('dynamicModal');
const modalContent = document.getElementById('modalContent');

// === ОСНОВНЫЕ ФУНКЦИИ ===

// Загрузка списка пользователей
async function loadUsers() {
    const response = await fetch(`${API_URL}/users`);
    const users = await response.json();

    usersTableBody.innerHTML = '';
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.lastName}</td>
            <td>${user.email}</td>
            <td>${user.roles.join(', ')}</td>
            <td><button class="btn btn-info btn-sm" onclick="openEditUserModal(${user.id})">Edit</button></td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Delete</button></td>
        `;
        usersTableBody.appendChild(row);
    });
}

// Получение списка ролей
async function getRoles() {
    const response = await fetch(`${API_URL}/roles`);
    return await response.json(); // массив строк: ["ROLE_USER", "ROLE_ADMIN"]
}

// Открытие формы добавления пользователя
async function openAddUserModal() {
    const roles = await getRoles();
    const formHTML = generateUserFormHTML({roles});
    modalContent.innerHTML = formHTML;
    $(dynamicModal).modal('show');

    setupFormSubmit(() => {
        $(dynamicModal).modal('hide');
        loadUsers();
    });
    console.log('Сгенерированная форма:', formHTML);
    console.log('modalContent после вставки:', modalContent.innerHTML);
}

// Открытие формы редактирования пользователя
async function openEditUserModal(userId) {
    const [userData, roles] = await Promise.all([
        fetch(`${API_URL}/users/${userId}`).then(res => res.json()),
        getRoles()
    ]);

    const formHTML = generateUserFormHTML({user: userData, roles});
    modalContent.innerHTML = formHTML;
    $(dynamicModal).modal('show');

    setupFormSubmit(() => {
        $(dynamicModal).modal('hide');
        loadUsers();
    }, userId);
}

// Удаление пользователя
async function deleteUser(userId) {
    if (!confirm('Вы уверены, что хотите удалить пользователя?')) return;

    await fetch(`${API_URL}/users/${userId}`, {
        method: 'DELETE'
    });

    await loadUsers();
}

// === ГЕНЕРАЦИЯ HTML ФОРМЫ ДОБАВЛЕНИЯ/РЕДАКТИРОВАНИЯ ПОЛЬЗОВАТЕЛЯ ===
function generateUserFormHTML({user = {}, roles = []}) {
    const isEdit = !!user.id;

    let roleCheckboxes = '';
    roles.forEach(role => {
        const checked = user.roles && user.roles.includes(role) ? 'checked' : '';
        roleCheckboxes += `
            <div class="form-check">
                <input type="checkbox" class="form-check-input" name="roles" value="${role}" ${checked}>
                <label class="form-check-label">${role}</label>
            </div>`;
    });

    return `
        <form id="userForm">
            <div class="modal-header">
                <h5 class="modal-title">${isEdit ? 'Edit User' : 'Add New User'}</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                ${isEdit ? `<input type="hidden" name="id" value="${user.id}">` : ''}
                
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" class="form-control" name="username" value="${user.username || ''}" required>
                </div>
                <div class="form-group">
                    <label>Last Name</label>
                    <input type="text" class="form-control" name="lastName" value="${user.lastName || ''}" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" class="form-control" name="email" value="${user.email || ''}" required>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" class="form-control" name="password" ${isEdit ? '' : 'required'}>
                </div>
                <div class="form-group">
                    <label>Roles</label>
                    ${roleCheckboxes}
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-success">${isEdit ? 'Update' : 'Add'}</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
            </div>
        </form>
    `;
}

// === ОБРАБОТЧИК ОТПРАВКИ ФОРМЫ ===
function setupFormSubmit(onSuccess, userId = null) {
    const form = document.getElementById('userForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const data = {
            username: formData.get('username'),
            lastName: formData.get('lastName'),
            email: formData.get('email'),
            password: formData.get('password') || undefined,
            roles: [...formData.getAll('roles')]
        };

        const method = userId ? 'PUT' : 'POST';
        const url = userId ? `${API_URL}/users/${userId}` : `${API_URL}/users`;

        await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        onSuccess();
    }, {once: true});
}

// === ИНИЦИАЛИЗАЦИЯ ===
document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});