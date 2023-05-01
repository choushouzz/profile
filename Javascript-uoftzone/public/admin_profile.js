
const removeUsers = []  //Array of Users to remove
const removePosts = [] //Array of posts to remove

log = console.log


//Get DOM from html 
const tableEntries = document.querySelector('#usersTable')
const removeEntries = document.querySelector('#usersAndposts')
const logOut = document.querySelector('#logout')
const logOutButton = document.querySelector('.logoutButton')

var posts
var users

//Entries wait for events
tableEntries.addEventListener('click', changeRemoveStatus)
removeEntries.addEventListener('click', remove)
logOut.addEventListener('click', adminLogOut)
logOutButton.addEventListener('click', adminLogOut)


/**call get function at beginning to get all data */
getCurrAdmin()
getUsersOrPosts()


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

    } else if (e.target.classList.contains('allPostsButton')) {
        navigateAdminProfileTabs('/admin_profile_posts')
    } else if (e.target.classList.contains('allUsersButton')) {
        navigateAdminProfileTabs('/admin_profile_users')
    }
}

/**External data functions */
/**The following functions  need obtain external severs data first
*/

//navigate btn admin_profile_users & admin_profile_posts
function navigateAdminProfileTabs(url) {

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

//admin log out
function adminLogOut() {
    const url = '/admins/logout';

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
               
                window.location.href = res.url;
            } else {
                console.log('status not 200')
                alert('log out not successful')

            }
            
        }).catch((error) => {
        alert('log out not successful')
        // loginNotSuccessful()
        console.log(error)
    })
}

function deleteInDB(){
    if (document.title == "CSC309 team54 project admin profile:users"){
        for (i = 1; i < tableEntries.children[0].childElementCount ; i++) {
            username = tableEntries.children[0].children[i].children[0].innerText
            
            same = users.filter(user => user.name == username)

            if (removeUsers.includes(username) && same.length > 0){
                user = same[0]
                deleteUsersOrPosts(user._id)
                index = users.indexOf(user);
                users.splice(index,1);

            }
        }
    }else if (document.title == "CSC309 team54 project admin profile:posts"){
        for (i = 1; i < tableEntries.children[0].childElementCount ; i++) {
           
            id = tableEntries.children[0].children[i].children[0].innerText
    
            same = posts.filter(post => post._id == id)
           
            if (removePosts.includes(id) && same.length > 0){
                post = same[0]
              
                deleteUsersOrPosts(post._id)
                index = posts.indexOf(post);
                posts.splice(index,1);

            }
        }
    }

}


function getUsersOrPosts() {
    var url
    if (document.title == "CSC309 team54 project admin profile:users"){
        url = '/users';
    }else if (document.title == "CSC309 team54 project admin profile:posts"){
        url = '/posts';
    }


    fetch(url)
    .then((res) => { 
        if (res.status === 200) {
           return res.json() 
       } else {
            alert('Could not get users or posts')
       }                
    })
    .then((json) => {
        if (document.title == "CSC309 team54 project admin profile:users"){
            json.users.map((u) => addNewUser(u) )
            users =  json.users
            
        }else if (document.title == "CSC309 team54 project admin profile:posts"){
            json.posts.map((u) =>addNewPost(u) )
            posts = json.posts
            
        }


        
    }).catch((error) => {
        console.log(error)
    })
}


function deleteUsersOrPosts(id) {
    log(id)
    var url
    if (document.title == "CSC309 team54 project admin profile:users"){
        url = '/users/' + id;
    }else if (document.title == "CSC309 team54 project admin profile:posts"){
        url = '/posts/' + id;
    }
    log(url)


    fetch(url, {
        method: 'delete'
    })
    .then((res) => { 
        if (res.status === 200) {
           return res.json() 
       } else {
            alert('Could not get Users or Posts')
       }                
    })
    .then((json) => {
        
            console.log(json)
         
        

        
    }).catch((error) => {
        console.log(error)
    })
}


function getCurrAdmin(){
    const url = '/getCurrAdmin'
    

     fetch(url)
    .then((res) => { 
        if (res.status === 200) {
            return res.json() 
       } else {
            alert('Could not get admin')
       }                
    })
    .then((json) => {

        admin = json[0]
        adminname = document.querySelector("#adminname")
        adminemail = document.querySelector("#adminemail")
    
        adminname.innerText = admin.name
        adminemail.innerText = admin.email
        
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
        if (checkpoint.innerText ==  "# of Posts" ){
            toremove = button.parentElement.parentElement.children[0].innerText
            removeUsers.push(toremove);

    
        } else{
            toremove = button.parentElement.parentElement.children[0].innerText
            removePosts.push(toremove);

    
        }
 
    }else if (button.name == "black"){
        button.name =  "white"
        button.style.backgroundColor = "white";
        checkpoint = button.parentElement.parentElement.parentElement.
        children[0].children[1];

    
    
        if (checkpoint.innerText ==  "# of Posts" ){
            toremove = button.parentElement.parentElement.children[0].innerText
            index = removeUsers.indexOf(toremove)
            removeUsers.splice(index,1);

    
        } else{
            toremove = button.parentElement.parentElement.children[1].innerText
            index =removePosts.indexOf(toremove);
            removePosts.splice(index,1);
        }
    }

}


function removeall(){
    checkpoint = tableEntries.children[0].children[0].children[1];

    if (checkpoint.innerText ==  "# of Posts" ){
        for (i = 1; i < tableEntries.children[0].childElementCount ; i++) {
            username = tableEntries.children[0].children[i].children[0].innerText
            if (removeUsers.includes(username)){
                toremove = tableEntries.children[0].children[i]
                tableEntries.children[0].removeChild(toremove)
                index =removeUsers.indexOf(username);
                removeUsers.splice(index,1);
                i--;
                
            }
        }
    }else{
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

}


function addNewUser(user){
    const name = user.name;
    const postNum = user.posts.length;
    const reportNum = user.reported;

    const tableRow = document.createElement('tr')

    const nameLabel = document.createElement('td')
	const nameText = document.createTextNode(name)

	nameLabel.appendChild(nameText)
	tableRow.appendChild(nameLabel)

	const postNumLabel = document.createElement('td')
	const postNumText = document.createTextNode(postNum)
	
	postNumLabel.appendChild(postNumText)
	tableRow.appendChild(postNumLabel)

    const reportNumLabel = document.createElement('td')
	const reportNumText = document.createTextNode(reportNum)
	
	reportNumLabel.appendChild(reportNumText)
    tableRow.appendChild(reportNumLabel)
    
    const checkLabel = document.createElement('td')
    button = document.createElement('button')
    button.className = "check"
    button.name = "white"
    
	checkLabel.appendChild(button) 
	tableRow.appendChild(checkLabel)
  
	tableEntries.children[0].append(tableRow)
}




function addNewPost(post){
    //const name = post.poster;
    const content = post.postContent;
    const reportNum = post.reported
    const id = post._id

    const tableRow = document.createElement('tr')

    
    const idLabel = document.createElement('td')
	const idText = document.createTextNode(id)
	
	idLabel.appendChild(idText)
	tableRow.appendChild(idLabel)

	const postLabel = document.createElement('td')
	const postText = document.createTextNode(content)
	
	postLabel.appendChild(postText)
    tableRow.appendChild(postLabel)
    

    const reportNumLabel = document.createElement('td')
	const reportNumText = document.createTextNode(reportNum)
	
	reportNumLabel.appendChild(reportNumText)
    tableRow.appendChild(reportNumLabel)
    
    const checkLabel = document.createElement('td')
    button = document.createElement('button')
    button.className = "check"
    button.name = "white"
    
	checkLabel.appendChild(button) 
	tableRow.appendChild(checkLabel)
  
	tableEntries.children[0].append(tableRow)

}





