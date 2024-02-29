
let phoneInput = document.getElementById('phone');

phoneInput.addEventListener('change', function() {

    localStorage.setItem('phone', phoneInput.value);
});


function proceedToCheckout() {
    let phoneInput = document.getElementById('phone');
    let phoneNumber = phoneInput.value.trim();
    let cartItems = document.querySelectorAll('.card.mb-4');


    if (phoneNumber === '') {
        alert('Пожалуйста, введите контактный номер телефона');
        return;

    }
    if (cartItems.length === 1) {
        alert('Ваша корзина пуста. Пожалуйста, добавьте товары перед переходом к оплате');
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
}