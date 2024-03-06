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
        "timeOut": "2500",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
});

$(document).ready(function() {
    $("#navBtn").click(function() {
        $(".main").toggleClass('animate');
    });
});

$(document).ready(function() {
    $('#createProductModal').on('show.bs.modal', function(event) {

        $('#productName').val('');
        $('#productDescription').val('');
        $('#productPrice').val('');
        $('#productCategory').val('');
    });

    $('#addProductForm').submit(function(event) {
        event.preventDefault();

        let productName = $('#productName').val();
        let productDescription = $('#productDescription').val();
        let productPrice = $('#productPrice').val();
        let productCategory = $('#productCategory').val();

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

                $('#createProductModal').modal('hide');

                toastr.success('Продукт успешно добавлен');

            },
            error: function(xhr, status, error) {

                toastr.error('Ошибка при добавлении продукта: ' + xhr.responseText);
            }
        });
    });
});

$(document).ready(function() {
    $('[id^="deleteProduct_"]').click(function() {
        let productId = this.id.split('_')[1];
        deleteProduct(productId);
    });
});

function deleteProduct(productId) {
    if (confirm('Are you sure you want to delete this product?')) {

        let url = "/api"
        let headers = {};

        $.ajax({
            type: 'DELETE',
            url: url + '/' + productId,
            headers: headers,
            success: function(response) {

                toastr.success('Продукт успешно удален');

                setTimeout(function() {
                    location.reload();
                }, 2500);
            },
            error: function(xhr, status, error) {
                if (xhr.status === 404) {
                    toastr.error('Ошибка при удалении продукта: ' + xhr.responseText);
                } else {
                    toastr.error("An error occurred while deleting the product. Please try again later." + xhr.responseText);
                }
            }
        });
    }
}

$(document).ready(function() {
    $('[id^="deleteOrder_"]').click(function() {
        let orderId = this.id.split('_')[1];
        deleteOrder(orderId);
    });
});

function deleteOrder(orderId) {
    if (confirm('Are you sure you want to delete this order?')) {

        let url = "/api/orders";
        let headers = {};

        $.ajax({
            type: 'DELETE',
            url: url + '/' + orderId,
            headers: headers,
            success: function(response) {
                toastr.success('Заказ успешно удален');

                setTimeout(function() {
                    location.reload();
                }, 2500);
            },
            error: function(xhr, status, error) {
                if (xhr.status === 404) {
                    toastr.error('Ошибка при удалении Заказа: ' + xhr.responseText);
                } else {
                    toastr.error("An error occurred while deleting the order. Please try again later." + xhr.responseText);
                }
            }
        });
    }
}


$(document).ready(function() {
    $('#changeStatusModal').on('show.bs.modal', function(event) {
        let button = $(event.relatedTarget);
        let orderId = button.data('order-id');

        $('#orderIdInput').val(orderId);
    });

    $('#changeStatusForm').submit(function(event) {
        event.preventDefault();

        let orderId = $('#orderIdInput').val();
        console.log(orderId)

        let newStatus = $('#newStatus').val();
        console.log(newStatus)

        $.ajax({
            type: 'PUT',
            url: '/api/orders/' + orderId,
            contentType: 'application/json',
            data: JSON.stringify({
                status: newStatus
            }),
            success: function(response) {

                $('#changeStatusModal').modal('hide');

                toastr.success('Order status updated successfully');

                setTimeout(function() {
                    location.reload();
                }, 2500);

            },
            error: function(xhr, status, error) {

                toastr.error('Error updating order status:', xhr.responseText);

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
                toastr.success('User deleted successfully');

                setTimeout(function() {
                    location.reload();
                }, 2500);
            },
            error: function(xhr, status, error) {
                toastr.error('Error deleting user:', xhr.responseText);

            }
        });
    }
}

$(document).ready(function() {
    $('#updateUserModal').on('show.bs.modal', function(event) {
        let button = $(event.relatedTarget);
        let userId = button.data('user-id');

        $('#userIdInput').val(userId);
    });

    $('#updateUserForm').submit(function(event) {
        event.preventDefault();

        let userId = $('#userIdInput').val();

        let newFirstName = $('#newFirstName').val();
        let newEmail = $('#newUserEmail').val();
        let newPhone = $('#newUserPhone').val();
        let newAddress = $('#newUserAddress').val();
        let newRole = $('#newUserRole').val();

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

                $('#updateUserModal').modal('hide');

                toastr.success('User updated successfully');

                setTimeout(function() {
                    location.reload();
                }, 2500);

            },
            error: function(xhr, status, error) {

                toastr.error('Error updating user:', xhr.responseText);

            }
        });
    });
});

$(document).ready(function() {
    $('#updateProductModal').on('show.bs.modal', function(event) {
        let button = $(event.relatedTarget);
        let productId = button.data('product-id');

        $('#productIdInput').val(productId);
    });

    $('#updateProductForm').submit(function(event) {
        event.preventDefault();

        let productId = $('#productIdInput').val();

        let newProductName = $('#newProductName').val();
        let newProductDescription = $('#newProductDescription').val();
        let newProductPrice = $('#newProductPrice').val();
        let newProductCategory = $('#newProductCategory').val();

        $.ajax({
            type: 'PUT',
            url: '/api/' + productId,
            contentType: 'application/json',
            data: JSON.stringify({
                name: newProductName,
                description: newProductDescription,
                price: newProductPrice,
                category: newProductCategory
            }),
            success: function(response) {

                $('#updateProductModal').modal('hide');

                toastr.success('Product updated successfully');

                setTimeout(function() {
                    location.reload();
                }, 2500);

            },
            error: function(xhr, status, error) {

                toastr.error('Error updating product:', xhr.responseText);

            }
        });
    });
});
