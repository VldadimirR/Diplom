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

    openConfirmDeleteModal();

    document.getElementById("confirmDeleteButton").addEventListener("click", function() {

        $.ajax({
            url: '/removeAllByFromCart',
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

        // Закрываем модальное окно
        closeConfirmDeleteModal();
    });


    document.getElementById("cancelDeleteButton").addEventListener("click", function() {

        console.log("Cancelled");

        closeConfirmDeleteModal();
    });
}


function openConfirmDeleteModal() {
    var modal = document.getElementById("confirmDeleteModal");
    modal.style.display = "block";
}


function closeConfirmDeleteModal() {
    var modal = document.getElementById("confirmDeleteModal");
    modal.style.display = "none";
}


document.getElementById("confirmDeleteButton").addEventListener("click", function() {

    console.log("Deleting...");

    closeConfirmDeleteModal();
});

document.getElementById("cancelDeleteButton").addEventListener("click", function() {
    // При нажатии "Отмена" ничего не происходит
    console.log("Cancelled");

    closeConfirmDeleteModal();
});

document.getElementsByClassName("close")[0].addEventListener("click", function() {

    console.log("Cancelled");

    closeConfirmDeleteModal();
});