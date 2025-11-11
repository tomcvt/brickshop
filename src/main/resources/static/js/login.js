const urlParams = new URLSearchParams(window.location.search);

if (urlParams.has('error')) {
    document.getElementById('login-alert').style.display = 'block';
}
if (urlParams.has('logout')) {
    document.getElementById('logout-alert').style.display = 'block';
}