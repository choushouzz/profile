
//* Event listeners for button submit and button click */
window.onload=function(){
  document.getElementById("submitButton").addEventListener("click", sub);
}


// Functions that don't edit DOM themselves, but can call DOM functions
function sub(e) {
    e.preventDefault();
    if (document.getElementById("newEmail").value != document.getElementById("repeatEmail").value) {
        alert('repeated email does not match your new email');
    }
    else if (document.getElementById("oldEmail").value == document.getElementById("newEmail").value) {
        alert('new email is the same as your old email! please choose another email');
    }
    else {
        const oldEmail = document.getElementById("oldEmail").value;
        const newEmail = document.getElementById("newEmail").value;
        const  url = '/updateEmail';
        const data = {oldEmail, newEmail}
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
                    alert('Your old email is incorrect!')
                }
                log(res)
            }).catch((error) => {
            console.log(error)
        })
    }
}
