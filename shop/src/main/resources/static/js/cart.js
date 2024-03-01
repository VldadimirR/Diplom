$(document).ready(function() {
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": false,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": true,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
});


let phoneInput = document.getElementById('phone');

phoneInput.addEventListener('change', function() {

    localStorage.setItem('phone', phoneInput.value);
});


function proceedToCheckout() {
    let phoneInput = document.getElementById('phone');
    let phoneNumber = phoneInput.value.trim();
    let cartItems = document.querySelectorAll('.card.mb-4');


    if (phoneNumber === '') {
        toastr.error('Пожалуйста, введите контактный номер телефона');
        return;

    }
    if (cartItems.length === 1) {

        toastr.warning('Ваша корзина пуста. Пожалуйста, добавьте товары перед переходом к оплате');
        return;
    }

    window.location.href = '/checkout';
}

function addToCart(button) {
    let productId = button.getAttribute("data-product-id");

    $.ajax({
        url: '/addToCart',
        type: 'POST',
        data: { productId: productId },
        success: function(response) {

            console.log(response);

            location.reload();
        },
        error: function(error) {

            console.error(error);
        }
    });
}

function removeFromCart(button) {
    let productId = button.getAttribute("data-product-id");

    $.ajax({
        url: '/removeFromCart',
        type: 'POST',
        data: { productId: productId },
        success: function(response) {

            console.log(response);

            location.reload();
        },
        error: function(error) {

            console.error(error);

        }
    });
}

function removeFromAllCart(button) {
    let productId = button.getAttribute("data-product-id");

    // Открытие модального окна подтверждения удаления
    openConfirmDeleteModal();

    // Обработчик кнопки "Да"
    document.getElementById("confirmDeleteButton").addEventListener("click", function() {
        // При нажатии "Да" выполняется удаление
        $.ajax({
            url: '/removeAllByFromCart',
            type: 'POST',
            data: { productId: productId },
            success: function(response) {
                console.log(response);
                location.reload(); // Перезагрузка страницы после успешного удаления
            },
            error: function(error) {
                console.error(error);
            }
        });

        // Закрываем модальное окно
        closeConfirmDeleteModal();
    });

    // Обработчик кнопки "Отмена"
    document.getElementById("cancelDeleteButton").addEventListener("click", function() {
        // При нажатии "Отмена" ничего не происходит
        console.log("Cancelled");
        // Закрываем модальное окно
        closeConfirmDeleteModal();
    });
}

// Открытие модального окна
function openConfirmDeleteModal() {
    var modal = document.getElementById("confirmDeleteModal");
    modal.style.display = "block";
}

// Закрытие модального окна
function closeConfirmDeleteModal() {
    var modal = document.getElementById("confirmDeleteModal");
    modal.style.display = "none";
}

// Обработчики кнопок "Да" и "Отмена"
document.getElementById("confirmDeleteButton").addEventListener("click", function() {
    // При нажатии "Да" выполняется удаление
    console.log("Deleting...");
    // Закрываем модальное окно
    closeConfirmDeleteModal();
});

document.getElementById("cancelDeleteButton").addEventListener("click", function() {
    // При нажатии "Отмена" ничего не происходит
    console.log("Cancelled");
    // Закрываем модальное окно
    closeConfirmDeleteModal();
});

// Обработчик кнопки "Отмена" в модальном окне
document.getElementsByClassName("close")[0].addEventListener("click", function() {
    // При нажатии крестика модальное окно также закрывается
    console.log("Cancelled");
    // Закрываем модальное окно
    closeConfirmDeleteModal();
});