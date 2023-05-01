const signIn = document.querySelector('#signinContent');
const username = document.querySelector('#username');
const password = document.querySelector('#password');
const signinForm = document.querySelector('#signinForm');
const home = document.querySelector('#loginBox');

//* Event listeners for button submit and button click */
signIn.addEventListener('click', allClickEvents);
home.addEventListener('click', allClickEvents);

// Functions that don't edit DOM themselves, but can call DOM functions
function userLogin() {
    const url = '/login';
    console.log('user login: username and pswd are ' + username + password)
    // The data we are going to send in our request
    let data = {
        name: username.value,
        password: password.value
    }
    // Create our request constructor with all the parameters we need
    const request = new Request(url, {
        method: 'post',
        body: JSON.stringify(data),
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        }
    });
    fetch(request)
        .then( (res) => {
            // Handle response we get from the API
            // Usually check the error codes to see what happened
            // const message = document.querySelector('#message')
            if (res.status === 200) {
                console.log(res.url)
                // message.innerText = 'Success: Added a student.'
                // message.setAttribute("style", "color: green")
                window.location.href = res.url;
            } else {
                console.log('status not 200')
                alert('log in not successful')
                // if (!signIn.lastElementChild.classList.contains('red')) {
                //     loginNotSuccessful();
                // }
            }
            console.log(res)
        }).catch((error) => {
        alert('log in not successful')
        // loginNotSuccessful()
        console.log(error)
    })
}

function userNotLogin() {
    const url = '/users/logout';

    // Create our request constructor with all the parameters we need
    const request = new Request(url, {
        method: 'get',
        // credentials: 'include', //include in actual log out method
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
    });
    fetch(request)
        .then((res) => {
            // Handle response we get from the API
            // Usually check the error codes to see what happened
            if (res.status === 200) {
                console.log(res)
                window.location.href = res.url;
            } else {
                console.log('status not 200')
                alert('log out not successful')

            }
            console.log(res)
        }).catch((error) => {
        alert('log out not successful')
        // loginNotSuccessful()
        console.log(error)
    })
}

function allClickEvents(e) {
    //e.preventDefault();
    if (e.target.id === 'submitButton' || e.target.id === 'submitLink') {
        //match the user and pswd through server
        console.log('username is ' + username.value + 'password is ' + password.value);

    }
}

// DOM functions

// This function adds the post to the postBox on the page that the user wants to post.
function loginNotSuccessful() {
    const loginNotSucceed = document.createElement('div');
    loginNotSucceed.className = 'red';
    loginNotSucceed.appendChild(document.createTextNode('Username and password does not match or username does not exist'));
    signIn.appendChild(loginNotSucceed);
}