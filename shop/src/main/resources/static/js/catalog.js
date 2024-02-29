$(document).ready(function() {
    // Получаем значение сортировки из URL
    let urlParams = new URLSearchParams(window.location.search);
    let selectedSort = urlParams.get('sort');
    let selectedCategory = urlParams.get('category');
    // Устанавливаем выбранную опцию в селекторе сортировки
    if (selectedSort) {
        $('#sortSelect').val(selectedSort);
    }
    // Устанавливаем выбранную опцию в селекторе
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

        currentUrl.searchParams.set("productName", productName);

        window.location.href = currentUrl.toString();

    });
});

$(document).ready(function() {
    // Обработчик события клика для кнопки сброса
    $('#resetButton').click(function() {
        // Удаляем параметры сортировки, категории и поиска из URL
        let currentUrl = new URL(window.location.href);
        currentUrl.searchParams.delete("sort");
        currentUrl.searchParams.delete("category");
        currentUrl.searchParams.delete("productName");

        // Обновляем страницу с новым URL без параметров фильтрации
        window.location.href = currentUrl.toString();
    });
});

function addToCart(button) {
    let productId = button.getAttribute("data-product-id");

    $.ajax({
        url: '/addToCart',
        type: 'POST',
        data: { productId: productId },
        success: function(response) {

            console.log(response);
        },
        error: function(error) {

            console.error(error);
        }
    });
}