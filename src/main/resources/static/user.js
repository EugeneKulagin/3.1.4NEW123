document.addEventListener("DOMContentLoaded", function () {
    getCurrentUser();
});

function getCurrentUser() {

    fetch("/api/user")
        .then( response => response.json())
        .then( user=> {
            const roles = user.roles.map(role => role.name.replace('ROLE_', ' '));
            document.getElementById("userEmail").textContent = user.username;
            document.getElementById("userRole").textContent = roles;

            let tableUser = ""
            tableUser += `
                <tr id="${user.id}" style="background-color: #E9ECEF;">
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${user.roles.map(role => role.name.replace('ROLE_', ' '))}</td>
                </tr>`

            console.log("qwerqwr")
            document.getElementById("userInfo").innerHTML = tableUser
        })
}