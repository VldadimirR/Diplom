$(document).ready(function() {
    $("#navBtn").click(function() {
        $(".main").toggleClass('animate');
    });
});

$(document).ready(function() {
    $('#createProductModal').on('show.bs.modal', function(event) {
        // Очистим поля формы перед открытием модального окна
        $('#productName').val('');
        $('#productDescription').val('');
        $('#productPrice').val('');
        $('#productCategory').val('');
    });

    $('#addProductForm').submit(function(event) {
        event.preventDefault(); // Предотвращаем стандартное поведение формы

        // Получаем данные из формы
        var productName = $('#productName').val();
        var productDescription = $('#productDescription').val();
        var productPrice = $('#productPrice').val();
        var productCategory = $('#productCategory').val();

        // Выполняем AJAX-запрос для создания нового продукта
        $.ajax({
            type: 'POST',
            url: '/api',
            contentType: 'application/json',
            data: JSON.stringify({
                name: productName,
                description: productDescription,
                price: productPrice,
                category: productCategory
            }),
            success: function(response) {
                // Обработка успешного создания продукта
                console.log('Product created successfully');
                location.reload(); // Пример перезагрузки страницы
            },
            error: function(xhr, status, error) {
                // Обработка ошибки создания продукта
                console.error('Error creating product:', xhr.responseText);
                // Выведите сообщение об ошибке или выполните другие действия
            }
        });
    });
});

function deleteOrder(orderId) {
    if (confirm('Are you sure you want to delete this order?')) {

        var url = "/api/orders";
        var headers = {};

        $.ajax({
            type: 'DELETE',
            url: url + '/' + orderId,
            headers: headers,
            success: function(response) {
                // Обновить страницу или список заказов после успешного удаления
                location.reload(); // Пример перезагрузки страницы
            },
            error: function(xhr, status, error) {
                if (xhr.status === 404) {
                    alert("This order cannot be deleted at the moment.");
                } else {
                    console.error(xhr.responseText);
                    alert("An error occurred while deleting the order. Please try again later.");
                }
            }
        });
    }
}

$(document).ready(function() {
    $('[id^="deleteOrder_"]').click(function() {
        var orderId = this.id.split('_')[1];
        deleteOrder(orderId);
    });
});

$(document).ready(function() {
    $('#changeStatusModal').on('show.bs.modal', function(event) {
        var button = $(event.relatedTarget); // Кнопка, которая открыла модальное окно
        var orderId = button.data('order-id'); // Получаем orderId из data-атрибута кнопки

        // Сохраняем orderId в скрытом поле формы
        $('#orderIdInput').val(orderId);
    });

    $('#changeStatusForm').submit(function(event) {
        event.preventDefault(); // Предотвращаем стандартное поведение формы

        // Получаем orderId из скрытого поля формы
        var orderId = $('#orderIdInput').val();
        console.log(orderId)

        // Получите новый статус заказа из формы
        var newStatus = $('#newStatus').val();
        console.log(newStatus)

        // Выполняем AJAX-запрос для обновления статуса заказа
        $.ajax({
            type: 'PUT',
            url: '/api/orders/' + orderId,
            contentType: 'application/json',
            data: JSON.stringify({
                status: newStatus
            }),
            success: function(response) {
                // Обработка успешного обновления статуса заказа
                console.log('Order status updated successfully');
                location.reload(); // Пример перезагрузки страницы
            },
            error: function(xhr, status, error) {
                // Обработка ошибки обновления статуса заказа
                console.error('Error updating order status:', xhr.responseText);
                // Выведите сообщение об ошибке или выполните другие действия
            }
        });
    });
});

function deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user?')) {
        $.ajax({
            type: 'DELETE',
            url: '/api/users/' + userId,
            success: function(response) {
                // Пользователь успешно удален, выполните необходимые действия
                console.log('User deleted successfully');
                // Перезагрузите страницу или выполните другие действия
                location.reload();
            },
            error: function(xhr, status, error) {
                // Обработка ошибки удаления пользователя
                console.error('Error deleting user:', xhr.responseText);
                alert('Unable to delete user. Please try again later.');
            }
        });
    }
}

$(document).ready(function() {
    $('#updateUserModal').on('show.bs.modal', function(event) {
        var button = $(event.relatedTarget); // Кнопка, которая открыла модальное окно
        var userId = button.data('user-id'); // Получаем userId из data-атрибута кнопки

        // При необходимости вы можете использовать userId для предварительного заполнения формы или выполнения других действий
        // Например, можно сделать AJAX-запрос для получения текущих данных пользователя и заполнить ими форму

        // Сохраняем userId в скрытом поле формы
        $('#userIdInput').val(userId);
    });

    $('#updateUserForm').submit(function(event) {
        event.preventDefault(); // Предотвращаем стандартное поведение формы

        // Получаем userId из скрытого поля формы
        var userId = $('#userIdInput').val();

        // Получите остальные данные из формы
        var newFirstName = $('#newFirstName').val();
        var newEmail = $('#newUserEmail').val();
        var newPhone = $('#newUserPhone').val();
        var newAddress = $('#newUserAddress').val();
        var newRole = $('#newUserRole').val(); // Предполагается, что у вас есть поле выбора роли в форме

        // Выполняем AJAX-запрос для обновления данных пользователя
        $.ajax({
            type: 'PUT',
            url: '/api/users/' + userId,
            contentType: 'application/json',
            data: JSON.stringify({
                firstname: newFirstName,
                email: newEmail,
                phone: newPhone,
                address: newAddress,
                role: newRole
            }),
            success: function(response) {
                // Обработка успешного обновления пользователя
                console.log('User updated successfully');
                location.reload();
            },
            error: function(xhr, status, error) {
                // Обработка ошибки обновления пользователя
                console.error('Error updating user:', xhr.responseText);
                // Выведите сообщение об ошибке или выполните другие действия
            }
        });
    });
});