
let storedContactPhone = localStorage.getItem('phone');


document.getElementById('hiddenPhone').value = storedContactPhone;

document.getElementById('displayedPhone').innerText = storedContactPhone;


let form = document.getElementById('orderForm');

form.addEventListener('click', function() {

    document.getElementById('submitButton').click();
});