document.addEventListener("DOMContentLoaded", function () {
    getCurrentAdmin()
    getAllUsers()

});

document.getElementById('formCreateNewUser').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    const rolesSelected = Array.from(document.getElementById('new_role').selectedOptions).map(option => ({
        id: option.value,
        name: option.text
    }))

    let newUser = {
        firstName: formData.get('new_firstName'),
        lastName: formData.get('new_lastName'),
        age: formData.get('new_age'),
        username: formData.get('new_email'),
        password: formData.get('new_password'),
        roles: rolesSelected
    }

    fetch('/api/admin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newUser)
    })
        .then(() => {
            getAllUsers()
            this.reset()
            document.getElementById('adminTable').click()
        })
})

function getCurrentAdmin() {
    fetch("/api/currentAdmin")
        .then( response => response.json())
        .then( user=> {
            const roles = user.roles.map(role => role.name.replace('ROLE_', ' '));
            document.getElementById("adminEmail").textContent = user.username;
            document.getElementById("adminRole").textContent = roles;

            let tableCurrentAdmin = ""
            tableCurrentAdmin += `
                <tr id="${user.id}">
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${user.roles.map(role => role.name.replace('ROLE_', ' '))}</td>
                </tr>`

            document.getElementById("adminInfo").innerHTML = tableCurrentAdmin
        })
}

function getAllUsers() {
    fetch("/api/admin")
        .then( response => response.json())
        .then( response => {
            let tableUser = ""
            response.forEach(user => {
                tableUser += `
                <tr id="${user.id}">
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${user.roles.map(role => role.name.replace('ROLE_', ' '))}</td>
                    <td>
                        <button type="button" class="btn btn-info" data-bs-toggle="modal" data-bs-target="#modalEdit"
                                onclick="getModalEdit(${user.id})" >Edit</button>
                    </td>
                   
                    <td>
                        <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#modalDelete"
                        onclick="getModalDelete(${user.id})">Delete</button>
                    </td>
                </tr>`
            })
            document.getElementById("allUsers").innerHTML = tableUser
        })
}

function getModalDelete(userId) {
    fetch(`/api/admin/${userId}`)
        .then( response => response.json())
        .then( user=> {
            document.getElementById('deleteUserId').value = user.id;
            document.getElementById('deleteFirstName').value = user.firstName;
            document.getElementById('deleteLastName').value = user.lastName;
            document.getElementById('deleteAge').value = user.age;
            document.getElementById('deleteEmail').value = user.email;
            document.getElementById('deletePassword').value = user.password;
        })
}

document.getElementById('formDeleteUser').addEventListener('submit', function (event) {
    event.preventDefault();

    let idDeleteUser = document.getElementById('deleteUserId').value

    fetch('/api/admin/'+idDeleteUser, {
        method: 'DELETE'
    })
        .then(() => {
            getAllUsers()

            document.getElementById('closeDeleteUser').click()
        })
})

function getModalEdit(userId) {
    fetch(`/api/admin/${userId}`)
        .then( response => response.json())
        .then( user=> {
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editFirstName').value = user.firstName;
            document.getElementById('editLastName').value = user.lastName;
            document.getElementById('editAge').value = user.age;
            document.getElementById('editEmail').value = user.email;
            document.getElementById('editPassword').value = user.password;
        })
}

document.getElementById('formEditUser').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);

    const rolesSelected = Array.from(document.getElementById('editRoles').selectedOptions).map(option => ({
        id: option.value,
        name: option.text
    }))

    let editUser = {
        id : formData.get('editUserId'),
        firstName: formData.get('editFirstName'),
        lastName: formData.get('editLastName'),
        age: formData.get('editAge'),
        username: formData.get('editEmail'),
        password: formData.get('editPassword'),
        roles: rolesSelected
    }

    fetch('/api/admin', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(editUser)
    })
        .then(() => {
            getAllUsers()
            this.reset()
            document.getElementById('closeEditUser').click()
        })
})