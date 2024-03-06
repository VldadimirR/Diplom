
let storedContactPhone = localStorage.getItem('phone');


document.getElementById('hiddenPhone').value = storedContactPhone;

document.getElementById('displayedPhone').innerText = storedContactPhone;


let form = document.getElementById('orderForm');

document.getElementById("confirmOrderButton").addEventListener("click", function(event) {
    event.preventDefault();

    // Отображаем модальное окно подтверждения заказа
    document.getElementById("confirmOrderModal").style.display = "block";
});

document.getElementById("confirmOrderYesButton").addEventListener("click", function() {
    // Если пользователь нажал "Да", отправляем форму
    document.getElementById("orderForm").submit();
});

document.getElementById("confirmOrderNoButton").addEventListener("click", function() {
    // Если пользователь нажал "Нет", скрываем модальное окно
    document.getElementById("confirmOrderModal").style.display = "none";
});

// Добавляем обработчик события для закрытия модального окна при нажатии на крестик
document.querySelector(".close").addEventListener("click", function() {

    document.getElementById("confirmOrderModal").style.display = "none";
});
