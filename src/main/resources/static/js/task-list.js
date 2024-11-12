document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.querySelector('.search-input');
    const searchButton = document.querySelector('.scroll-page-btn');

    if (searchInput && searchButton) {
        const urlParams = new URLSearchParams(window.location.search);
        const searchQuery = urlParams.get('search');

        if (searchQuery && searchQuery.trim() !== '') {
            searchButton.textContent = 'Clear Search';
            searchButton.addEventListener('click', function (event) {
                event.preventDefault();
                window.location.href = '/tasks?search=';
            });
        } else {
            searchButton.textContent = 'Search';
        }
    }
});