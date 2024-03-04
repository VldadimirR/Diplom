fetch('/api/orders/status')
    .then(response => response.json()) 
    .then(data => {

        const orderData = {
            labels: ['CREATE', 'IN_PROCESS', 'COMPLETED'],
            datasets: [{
                label: 'Order Status',
                data: [data.CREATE, data.IN_PROCESS, data.COMPLETED],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                ],
                borderWidth: 1
            }]
        };

        const ctx = document.getElementById('orderStatusChart').getContext('2d');

        const orderStatusChart = new Chart(ctx, {
            type: 'doughnut',
            data: orderData,
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });


fetch('/api/users/roles')
    .then(response => response.json())
    .then(data => {

        const userData = {
            labels: ['ROLE_ADMIN', 'ROLE_USER'],
            datasets: [{
                label: 'User Roles',
                data: [data.ROLE_ADMIN, data.ROLE_USER],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                ],
                borderWidth: 1
            }]
        };

        const ctx = document.getElementById('userRolesChart').getContext('2d');

        const userRolesChart = new Chart(ctx, {
            type: 'doughnut',
            data: userData,
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });


fetch('/api/price')
    .then(response => response.json())
    .then(data => {
        const productData = {
            labels: ['< $50', '$50 - $100', '$100 - $150', '$200+'],
            datasets: [{
                label: 'Product Prices',
                data: [data.lessThan50, data.between50And100, data.between100And150, data.over200],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                ],
                borderWidth: 1
            }]
        };

        const chartOptions = {
            responsive: true,
            maintainAspectRatio: false
        };

        const ctx = document.getElementById('productPriceChart').getContext('2d');

        const productPriceChart = new Chart(ctx, {
            type: 'bar',
            data: productData,
            options: chartOptions
        });
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });


fetch('/api/orders/count-by-date')
    .then(response => response.json())
    .then(data => {
        const dates = Object.keys(data);
        const counts = Object.values(data);

        const ctx = document.getElementById('orderCountChart').getContext('2d');
        const orderCountChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: dates,
                datasets: [{
                    label: 'Number of Orders',
                    data: counts,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });
    })
    .catch(error => {
        console.error('Error fetching order count data:', error);
    });


fetch('/api/orders/count-by-user-auth-status')
    .then(response => response.json())
    .then(data => {
        const userAuthStatuses = Object.keys(data);
        const counts = Object.values(data);

        const labels = userAuthStatuses.map(status => status === 'Anonymous' ? 'Anonymous Users' : 'Authenticated Users');

        const ctx = document.getElementById('orderCountByUserAuthStatusChart').getContext('2d');
        const orderCountByUserAuthStatusChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Number of Orders placed by authorized and unauthorized users',
                    data: counts,
                    backgroundColor: ['rgba(75, 192, 192, 0.2)', 'rgba(54, 162, 235, 0.2)'],
                    borderColor: ['rgba(75, 192, 192, 1)', 'rgba(54, 162, 235, 1)'],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });
    })
    .catch(error => {
        console.error('Error fetching order count by user auth status:', error);
    });


fetch('/api/count-by-category')
    .then(response => response.json())
    .then(data => {
        const categories = Object.keys(data);
        const counts = Object.values(data);

        const ctx = document.getElementById('productCountByCategoryChart').getContext('2d');
        const productCountByCategoryChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: categories,
                datasets: [{
                    label: 'Product Count by Category',
                    data: counts,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    })
    .catch(error => {
        console.error('Error fetching product count by category data:', error);
    });


