
//* Event listeners for button submit and button click */
window.onload=function(){
  document.getElementById("submitButton").addEventListener("click", sub);
}


// Functions that don't edit DOM themselves, but can call DOM functions
function sub(e) {
    e.preventDefault();
    if (document.getElementById("newPassword").value != document.getElementById("repeatPassword").value) {
        alert('repeated password does not match your new password');
    }
    else if (document.getElementById("oldPassword").value == document.getElementById("newPassword").value) {
        alert('new password is the same as your old password! please choose another password');
    }
    else {
        const oldPassword = document.getElementById("oldPassword").value;
        const newPassword = document.getElementById("newPassword").value;
        const  url = '/updatePassword';
        const data = {oldPassword, newPassword}
        const request = new Request(url, {
        method: 'PATCH',
        body: JSON.stringify(data),
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
        });
        fetch(request)
            .then((res) => {
                // Handle response we get from the API
                if (res.status == 200) {
                    alert('Success')
                }
                else {
                    alert('Your old Password is incorrect!')
                }
                log(res)
            }).catch((error) => {
            console.log(error)
        })
    }
}
