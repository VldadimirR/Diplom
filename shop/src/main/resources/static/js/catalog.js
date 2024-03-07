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
        "timeOut": "1500",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
});

$(document).ready(function() {
    let urlParams = new URLSearchParams(window.location.search);
    let selectedSort = urlParams.get('sort');
    let selectedCategory = urlParams.get('category');

    if (selectedSort) {
        $('#sortSelect').val(selectedSort);
    }

    if (selectedCategory) {
        $('#categorySelect').val(selectedCategory);
    }

    $('#sortSelect').change(function() {
        let selectedSort = $('#sortSelect').val();
        let currentUrl = new URL(window.location.href);
        currentUrl.searchParams.delete("productName");
        currentUrl.searchParams.set("sort", selectedSort);

        window.location.href = currentUrl.toString();

    });
});


$(document).ready(function(){
    $('#categorySelect').change(function () {
        let selectedCategory = $('#categorySelect').val();
        let currentUrl = new URL(window.location.href);

        currentUrl.searchParams.delete("productName");
        currentUrl.searchParams.set("page", "0");

        currentUrl.searchParams.set("category", selectedCategory);

        window.location.href = currentUrl.toString();
    });
});

$(document).ready(function(){
    $('#searchButton').click(function() {

        let productName = $('#productNameInput').val();
        let currentUrl = new URL(window.location.href);

        currentUrl.searchParams.delete("sort");
        currentUrl.searchParams.delete("category");
        currentUrl.searchParams.set("page", "0");


        currentUrl.searchParams.set("productName", productName);

        window.location.href = currentUrl.toString();

    });
});

$(document).ready(function() {
    $('#resetButton').click(function() {

        $('#dialogOverlay').fadeIn();
    });

    $('#confirmButton').click(function() {

        let currentUrl = new URL(window.location.href);

        currentUrl.searchParams.delete("sort");
        currentUrl.searchParams.delete("category");
        currentUrl.searchParams.delete("productName");

        window.location.href = currentUrl.toString();

        $('#dialogOverlay').fadeOut();
    });

    $('#cancelButton').click(function() {

        $('#dialogOverlay').fadeOut();
        return false;
    });
});

function addToCart(button) {
    let productId = button.getAttribute("data-product-id");

    $.ajax({
        url: '/addToCart',
        type: 'POST',
        data: { productId: productId },
        success: function(response) {

            toastr.success('Товар успешно добавлен в корзину!');
            console.log(response);
        },
        error: function(error) {
            console.error(error);
        }
    });
}