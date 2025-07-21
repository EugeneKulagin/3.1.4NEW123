document.addEventListener('DOMContentLoaded', function () {
    fetch('/user/user_profile')
        .then(response => response.json())
        .then(user => {
            // Заполняем хедер
            document.getElementById('header-email').textContent = user.email;
            document.getElementById('header-roles').textContent = user.roles.join(', ');

            // Заполняем таблицу
            document.getElementById('userId').textContent = user.id;
            document.getElementById('userUsername').textContent = user.username;
            document.getElementById('userLastName').textContent = user.lastName;
            document.getElementById('userEmail').textContent = user.email;
            document.getElementById('userRoles').textContent = user.roles.join(', ');
        })
        .catch(error => {
            console.error('Ошибка при загрузке данных:', error);
        });
});