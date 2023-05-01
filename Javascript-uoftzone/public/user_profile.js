/* admin js for team 54 project */

url = '/getCurrUser';
fetch(url)
    .then((res) => { 
        if (res.status === 200) {
           return res.json() 
       } else {
            alert('Could not get students')
       }                
    })
    .then((json) => {
        // log(json[0])
        const userInfo = document.querySelector('#profileInf');
        userInfo.children[0].children[0].innerText = json[0].name;
        userInfo.children[1].children[0].innerText = json[0].email;
    }).catch((error) => {
        console.log(error)
    })

const removePosts = [] //Array of posts to remove
var posts //var to keep posts
var user

//Get DOM from html 
const tableEntries = document.querySelector('#postTable')
const removeEntries = document.querySelector('#posts')
const logOut = document.querySelector('#logout')
const logOutButton = document.querySelector('#logOutButton')

//Entries wait for events
tableEntries.addEventListener('click', changeRemoveStatus)
removeEntries.addEventListener('click', remove)
logOut.addEventListener('click', userLogOut)
logOutButton.addEventListener('click', userLogOut)

log = console.log
getCurrUser()

/**Local use functions */
/**The following functions does not need external severs data
 and can be used just using local data
*/
function changeRemoveStatus(e) {
    e.preventDefault();
    if (e.target.classList.contains('check') ) {
        changeRemoveButton(e.target)
    }
}

function remove(e) {
    e.preventDefault();
    if (e.target.classList.contains('remove') ) {

        deleteInDB()
        removeall()
     

    }

}

/**External data functions */
/**The following functions  need obtain external severs data first
 and can not be functional just using local data
*/

function userLogOut() {
    const url = '/users/logout';

    // Create our request constructor with all the parameters we need
    const request = new Request(url, {
        method: 'get',
        credentials: 'include', //include in actual log out method so that cookies can be erased
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
    });
    console.log('making a request to log out')
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




/**External data functions */
/**The following functions  need obtain external severs data first
*/
function deleteInDB(){
    
    for (i = 1; i < tableEntries.children[0].childElementCount ; i++) {
            content = tableEntries.children[0].children[i].children[0].innerText
            likes = tableEntries.children[0].children[i].children[1].innerText
            replies = tableEntries.children[0].children[i].children[2].innerText
            //users = getUsersOrPosts()
            //console.log(users)
    
            same = posts.filter(post =>
                post.likes == likes &&
                post.replies.length == replies &&
                post.postContent == content)
            
            console.log(same)
            if (removePosts.includes(content) && same.length > 0){
                post = same[0]
                deletePosts(post._id)
                deleteUserPost(post._id)
                index = user.posts.indexOf(post);
                user.posts.splice(index,1);

            }
        }
    

}



function deleteUserPost(id) {
    var url = '/deletePost/' + id;
    


    fetch(url, {
        method: 'delete'
    })
    .then((res) => { 
        if (res.status === 200) {
           return res.json() 
       } else {
            alert('Could not get id')
       }                
    })
    .then((json) => {
        
            console.log(json)
         

        
    }).catch((error) => {
        console.log(error)
    })
}



function deletePosts(id) {
    var url = '/posts/' + id;
    


    fetch(url, {
        method: 'delete'
    })
    .then((res) => { 
        if (res.status === 200) {
           return res.json() 
       } else {
            alert('Could not get id')
       }                
    })
    .then((json) => {
        
            console.log(json)
         

        
    }).catch((error) => {
        console.log(error)
    })
}


function getCurrUser(){
    const url = '/getCurrUser'
    

     fetch(url)
    .then((res) => { 
        if (res.status === 200) {
            return res.json() 
       } else {
            alert('Could not get user')
       }                
    })
    .then((json) => {
        user = json[0]
        username = document.querySelector("#username")
        useremail = document.querySelector("#useremail")
        log(user)
        username.innerText = user.name
        useremail.innerText = user.email
        posts = user.posts
        user.posts.map((u) =>addNewPost(u) )
        
        return json[0]
        
    }, (error) => {
        log(error);
    })
}












/**DOM functions */
/**The following functions are used to change DOM elements*/
function changeRemoveButton(button){

    if (button.name == "white"){
        button.name =  "black"
        button.style.backgroundColor = "black";
        checkpoint = button.parentElement.parentElement.parentElement.
        children[0].children[1];
        toremove = button.parentElement.parentElement.children[0].innerText
        removePosts.push(toremove);

    
        
 
    }else if (button.name == "black"){
        button.name =  "white"
        button.style.backgroundColor = "white";
        checkpoint = button.parentElement.parentElement.parentElement.
        children[0].children[1];
        toremove = button.parentElement.parentElement.children[0].innerText
        index =removePosts.indexOf(toremove);
        removePosts.splice(index,1);
        
    

    }
}


function removeall(){
    for (i = 1; i < tableEntries.children[0].childElementCount ; i++) {
        postid = tableEntries.children[0].children[i].children[0].innerText
        if (removePosts.includes(postid)){
            toremove = tableEntries.children[0].children[i]
            tableEntries.children[0].removeChild(toremove)
            index =removePosts.indexOf(postid);
            removePosts.splice(index,1);
            i--;

        }
    }

}






function addNewPost(post){
    const likes = post.likes;
    const title = post.postContent;
    const replyNum = post.replies.length;

    const tableRow = document.createElement('tr')

    const nameLabel = document.createElement('td')
	const nameText = document.createTextNode(title)

	nameLabel.appendChild(nameText)
	tableRow.appendChild(nameLabel)

	const postLabel = document.createElement('td')
	const postText = document.createTextNode(likes)
	
	postLabel.appendChild(postText)
	tableRow.appendChild(postLabel)

    const replyNumLabel = document.createElement('td')
	const replyNumText = document.createTextNode(replyNum)
	
	replyNumLabel.appendChild(replyNumText)
    tableRow.appendChild(replyNumLabel)
    
    const checkLabel = document.createElement('td')
    button = document.createElement('button')
    button.className = "check"
    button.name = "white"
    
	checkLabel.appendChild(button) 
	tableRow.appendChild(checkLabel)
  
	tableEntries.children[0].append(tableRow)

}







