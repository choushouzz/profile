'use strict';
const log = console.log;

const express = require('express')
const port = process.env.PORT || 3000
const bodyParser = require('body-parser') // middleware for parsing HTTP body from client
const session = require('express-session')
const hbs = require('hbs')
const app = express();
const path = require('path')
app.use(bodyParser.json());

const {ObjectID} = require('mongodb')

// Import our mongoose connection
const {mongoose} = require('./db/mongoose');

// Import the models
const {Post} = require('./model/post')
const {User} = require('./model/user')
const {Admin} = require('./model/admin')

app.use(express.static("public"));

// Add express sesssion middleware
app.use(session({
    name: 'sid',
    secret: 'oursecret',
    resave: false,
    saveUninitialized: false,
    cookie: {
        maxAge: 1000 * 60 * 60 * 2, //2 hours
        httpOnly: true,
        sameSite: true
    }
}))


// route for dashboard
// Add middleware to check for not logged-in users
// Add middleware to check for logged-in users

// Authentication
const authenticateUser = (req, res, next) => {
    if (req.session.user) {
        User.findById(req.session.user).then((user) => {
            if (!user) {
                return Promise.reject()
            } else {
                req.user = user
                next()
            }
        }).catch((error) => {
            res.redirect('/')
        })
    } else {
        res.redirect('/')
    }
}

// Middleware for authentication for resources
const authenticateAdmin = (req, res, next) => {
    if (req.session.admin === 1) {
        req.admin = 1
        next()
    }
    else {
        res.redirect('/')
    }
}

//to check if user or admin exists, directly jumped to dashboard if so
const sessionChecker = (req, res, next) => {
    if (req.session.user || req.session.admin) {
        res.redirect('logged_q')
    } else {
        next();
    }
}

// Add middleware to check if user/admin logs in and redirect to index if not for user
const checkLoggedIn = (req, res, next) => {
    if (!req.session.user) {
        res.redirect('/')
    } else {
        next();
    }
}

//check if admin is logged in
const checkAdminLoggedIn = (req, res, next) => {
    if (!req.session.admin) {
        console.log('admin does not exist!')
        res.redirect('/')
    } else {
        console.log('admin exists!')
        next();
    }
}

app.get('/logged_q', checkLoggedIn, (req, res) => {
    log('send logged_q')
    res.sendFile(__dirname + '/public/logged_q.html')
})

app.get('/index', sessionChecker, (req, res) => {
    res.sendFile(path.join(__dirname + '/public/index.html'))
})

app.get('/', sessionChecker, (req, res) => {
    res.redirect('/index')
})


//Navigation for  logout
app.get('/users/logout', authenticateUser, (req, res) => {
    req.session.destroy((error) => {
        if (error) {
            res.status(500).send(error)
        } else {
            res.clearCookie('sid')
            res.redirect('/')
        }
    })
})

//use what's in admin_server to check authenticaZtion of admin_Server
//Get all users
app.get('/getAllUser', authenticateUser, (req, res) => {
    User.find().then((user) => {
        res.send(user)
    }, (error) => {
        res.status(500).send(error)
    })
})


//Navigation for guest

app.get('/guest_b', sessionChecker, (req, res) => {
    res.sendFile(__dirname + '/public/not_logged_b.html')
})


app.get('/guest_f', sessionChecker, (req, res) => {
    res.sendFile(__dirname + '/public/not_logged_f.html')
})

//Navigatio for user
app.get('/user_profile', authenticateUser, (req, res) => {
    if (req.user) {
        req.session.user = req.user._id;
        log(req.admin)
        log(req.session.admin)
        res.sendFile(__dirname + '/public/user_profile.html') //put the admin html
    } else {
        res.redirect('/')
    }
})


app.get('/logged_q', checkLoggedIn, (req, res) => {
    res.sendFile(__dirname + '/public/logged_q.html')
})

app.get('/logged_b', checkLoggedIn, (req, res) => {
    res.sendFile(__dirname + '/public/logged_b.html')
})

app.get('/logged_f', checkLoggedIn, (req, res) => {
    res.sendFile(__dirname + '/public/logged_f.html')
})


//Navigation for admin

//go to admin profile users
app.get('/admin_profile_users', authenticateAdmin, (req, res) => {
    if (req.admin) {
        req.session.admin = req.admin;
        log(req.admin)
        log(req.session.admin)
        res.sendFile(__dirname + '/public/admin_profile_users.html')
    } else {
        res.redirect('/')
    }
})

//go to admin profile posts
app.get('/admin_profile_posts', authenticateAdmin, (req, res) => {
    if (req.admin) {
        req.session.admin = req.admin;
        log(req.admin)
        log(req.session.admin)
        res.sendFile(__dirname + '/public/admin_profile_posts.html')
    } else {
        res.redirect('/')
    }
})

//Admin side navagation
app.get('/admin/change_email', authenticateAdmin, (req, res) => {
    res.sendFile(__dirname + '/public/change_email_admin.html')
})

app.get('/admin/change_password', authenticateAdmin, (req, res) => {
    res.sendFile(__dirname + '/public/change_pswd_admin.html')
})

app.get('/admin_q', checkAdminLoggedIn, (req, res) => {
    res.sendFile(__dirname + '/public/admin_q.html')
})

app.get('/user/change_email', authenticateUser, (req, res) => {
    res.sendFile(__dirname + '/public/change_email.html')
})

app.get('/user/change_password', authenticateUser, (req, res) => {
    res.sendFile(__dirname + '/public/change_pswd.html')
})

app.get('/admin_b', checkAdminLoggedIn, (req, res) => {
    res.sendFile(__dirname + '/public/admin_b.html')
})

app.get('/admin_f', checkAdminLoggedIn, (req, res) => {
    res.sendFile(__dirname + '/public/admin_f.html')
})

app.get('/admins/logout', authenticateAdmin, (req, res) => {
    req.session.destroy((error) => {
        if (error) {
            res.status(500).send(error)
        } else {
            res.clearCookie('sid')
            res.redirect('/')
        }
    })
})

// route for sign up
app.get('/signup', sessionChecker, (req, res) => {
    res.sendFile(__dirname + '/public/sign_up.html')
})

// route for login
app.get('/login', sessionChecker, (req, res) => {
    res.sendFile(__dirname + '/public/sign_in.html')
})

//user admin log in
app.post('/login', sessionChecker, (req, res) => {
    const name = req.body.name
    const password = req.body.password
    log('name pswd: ' + typeof name + typeof password)

    if (!(name.toLowerCase() === 'admin')) {
        User.findByNamePassword(name, password).then((user) => {
            if (!user) {
                log('no matching user :(')
                res.redirect('/login')
            } else {
                // Add the user to the session cookie that we will
                // send to the client
                req.session.user = user._id;
                log(req.session.user)
                // req.session.name = user.name
                log('found user :)')
                res.redirect('/logged_q')
            }
        }).catch((error) => {
            log(error)
            res.status(400).send(error)
        })
    } else if (name.toLowerCase() === 'admin' && password.toLowerCase() === 'admin') {
        req.session.admin = 1;
        // req.session.name = admin.name
        log('found admin :) admin id: ' + req.session.admin)
        res.redirect('/admin_q')
    }
})

//user sign up
app.post('/users', (req, res) => {
    // Add code here
    const user = new User({
        name: req.body.name,
        email: req.body.email,
        password: req.body.password,
        posts: req.body.posts,
        reported: req.body.reported
    })

    user.save().then((result) => {
        res.redirect('/login')
        // res.send(result)
    }, (error) => {
        res.status(400).send(error) // 400 for bad request
    })
})


app.get('/users', authenticateAdmin, (req, res) => {
    log('I have reached here')
    User.find().then((users) => {
        res.send({users}) // put in object in case we want to add other properties
    }, (error) => {
        res.status(500).send(error)
    })


})


//Delete a user
app.delete('/users/:id', authenticateAdmin, (req, res) => {
    const id = req.params.id

    // Good practise is to validate the id
    if (!ObjectID.isValid(id)) {
        return res.status(404).send()
    }

    // Otheriwse, findByIdAndRemove
    User.findByIdAndRemove(id).then((user) => {
        if (!user) {
            res.status(404).send()
        } else {
            res.send({user})
        }
    }).catch((error) => {
        res.status(400).send(error)
    })


})

//Make a post
app.post('/posts', authenticateUser, (req, res) => {
    const post = new Post({
        likes: req.body.likes,
        reported: req.body.reported,
        replies: req.body.replies,
        poster: req.body.poster,
        postContent: req.body.postContent,
        category: req.body.category
    })

    post.save().then((result) => {
        res.send(result)
    }, (error) => {
        res.status(400).send(error) // 400 for bad request
    })
})

//Get all posts
app.get('/posts', authenticateAdmin, (req, res) => {
    log('I have reached here')
    Post.find().then((posts) => {
        res.send({posts})
    }, (error) => {
        res.status(500).send(error)
    })
})

//Make reply to a post
app.post('/posts/:id', authenticateUser, (req, res) => {

    const id = req.params.id

    const post = new Post({
        likes: req.body.likes,
        reported: req.body.reported,
        replies: req.body.replies,
        poster: req.body.poster,
        postContent: req.body.postContent,
        category: req.body.category
    })


    if (!ObjectID.isValid(id)) {
        res.status(404).send()
    }


    Post.findOneAndUpdate({_id: req.params.id}, {
        $push: {replies: post}
    }, {new: true}).then((reply) => {
        if (!reply) {
            res.status(404).send()
        } else {


            res.send(reply)
        }
    }).catch((error) => {
        res.status(400).send(error)
    })
})

//Delete a post from user's list
app.delete('/deletePost/:id', checkLoggedIn, async (req, res) => {
    const id = req.params.id


    User.findOneAndUpdate({
        _id: new ObjectID(req.session.user)
    }, {

        $pull: {posts: {"_id": id}}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });


})


//Delete a post and all its replies
app.delete('/posts/:id', authenticateAdmin, (req, res) => {
    const id = req.params.id

    if (!ObjectID.isValid(id)) {
        return res.status(404).send()
    }


    // Otheriwse, findByIdAndRemove
    Post.findByIdAndRemove(id).then((post) => {
        if (!post) {
            res.status(404).send()
        } else {
            const replies = post.replies

            replies.map(reply => {
                Post.deleteOne({"_id": reply._id}).then((post) => {
                    if (!post) {
                        res.status(404).send()
                    } else {
                        res.send({post})
                    }
                })
            })
            res.send({post})
        }
    }).catch((error) => {
        res.status(400).send(error)
    })

})


app.patch('/liked', authenticateUser, async (req, res) => {

    // find the id of the post
    let allposts = [];
    await Post.find().then((post) => {
        allposts = post
    }, (error) => {
        res.status(500).send(error)
    })
    const beingmodified = allposts.filter((post) => (post.postContent == req.body.postContent) &&
        (post.category == req.body.category))

    // update the post's like attribute
    Post.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {
            likes: 1
        }
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

    // update the information in poster's post list
    User.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0].poster), "posts._id": new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {"posts.$.likes": 1}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

})

app.patch('/unliked', authenticateUser, async (req, res) => {

    // find the id of the post
    let allposts = [];
    await Post.find().then((post) => {
        allposts = post
    }, (error) => {
        res.status(500).send(error)
    })
    const beingmodified = allposts.filter((post) => (post.postContent == req.body.postContent) &&
        (post.category == req.body.category))

    // update the post's like attribute
    Post.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {
            likes: -1
        }
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

    // update the information in poster's post list
    User.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0].poster), "posts._id": new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {"posts.$.likes": -1}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

})

app.patch('/report', authenticateUser, async (req, res) => {

    // find the id of the post
    let allposts = [];
    await Post.find().then((post) => {
        allposts = post
    }, (error) => {
        res.status(500).send(error)
    })
    const beingmodified = allposts.filter((post) => (post.postContent == req.body.postContent) &&
        (post.category == req.body.category))
    log(req.body)
    log(allposts)
    log(beingmodified)
    // update the post's report attribute
    Post.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {
            reported: 1
        }
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

    // update the information in poster's post list
    User.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0].poster), "posts._id": new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {"posts.$.reported": 1, reported: 1}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

})


app.patch('/unreport', authenticateUser, async (req, res) => {

    // find the id of the post
    let allposts = [];
    await Post.find().then((post) => {
        allposts = post
    }, (error) => {
        res.status(500).send(error)
    })
    const beingmodified = allposts.filter((post) => (post.postContent == req.body.postContent) &&
        (post.category == req.body.category))

    // update the post's report attribute
    Post.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {
            reported: -1
        }
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

    // update the information in poster's post list
    User.findOneAndUpdate({
        _id: new ObjectID(beingmodified[0].poster), "posts._id": new ObjectID(beingmodified[0]._id)
    }, {
        // update operators: $set and $inc
        $inc: {"posts.$.reported": -1, reported: -1}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });

})

app.get('/getCurrUser', checkLoggedIn, (req, res) => {

    User.find({_id: req.session.user}).then(user => {
        if (user) {
            res.send(user)
        }
        else {
            res.status(404).send()
        }
    }, (error) => {
        log(error)
    })
})

app.get('/getCurrAdmin', checkAdminLoggedIn, (req, res) => {

    Admin.find({_id: req.session.admin}).then(admin => {
        if (admin) {
            res.send(admin)
        }
        else {
            res.status(404).send()
        }
    }, (error) => {
        log(error)
    })
})

app.post('/addPost', checkLoggedIn, async (req, res) => {
    const post = new Post({
        likes: req.body.likes,
        reported: req.body.reported,
        replies: req.body.replies,
        poster: req.body.poster,
        postContent: req.body.postContent,
        category: req.body.category
    })
    await post.save().then((result) => {
        res.send(result)
    }, (error) => {
        res.status(400).send(error) // 400 for bad request
    })

    // update the user's post list
    let target = {}
    await Post.find({'postContent': req.body.postContent, 'category': req.body.category}).then(post => {
        if (post) {
            target = post[0];
        }
    }, (error) => {
        log(error)
    })

    User.findOneAndUpdate({
        _id: new ObjectID(req.session.user)
    }, {
        // update operators: $set and $inc
        $push: {posts: target}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        log(result)
    });
})

app.post('/addReply', checkLoggedIn, async (req, res) => {
    //1.save post to database
    const post = new Post({
        likes: req.body.newpost.likes,
        reported: req.body.newpost.reported,
        replies: req.body.newpost.replies,
        poster: req.body.newpost.poster,
        postContent: req.body.newpost.postContent,
        category: req.body.newpost.category
    })
    await post.save().then((result) => {
        res.send(result)
    }, (error) => {
        res.status(400).send(error) // 400 for bad request
    })

    //2.update the parent post's reply list
    let newpost = [];
    let oldpost = [];
    await Post.find({'postContent': req.body.newpost.postContent, 'category': req.body.newpost.category}).then(post => {
        if (post) {
            newpost = post;
        }
    }, (error) => {
        log(error)
    })
    await Post.findOneAndUpdate({
        'postContent': req.body.oldpost.postContent, 'category': req.body.oldpost.category
    }, {
        // update operators: $set and $inc
        $push: {"replies": newpost[0]}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        // log(result)
    });


    // 3. update the post list of the write who is replying(current logged in user)
    await User.findOneAndUpdate({
        _id: new ObjectID(req.session.user)
    }, {
        // update operators: $set and $inc
        $push: {posts: newpost[0]}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        // log(result)
    });

    // // 4. update the post list of the author of being replied, by using the result from 2
    await Post.find({'postContent': req.body.oldpost.postContent, 'category': req.body.oldpost.category}).then(post => {
        if (post) {
            oldpost = post;
        }
    }, (error) => {
        log(error)
    })
    log(oldpost);
    await User.findOneAndUpdate(
        {$and: [{"_id": new ObjectID(oldpost[0].poster)}, {"posts.postContent": oldpost[0].postContent}, {"posts.category": oldpost[0].category}]}, {
            // update operators: $set and $inc
            $push: {"posts.$.replies": newpost[0]}
        }, {
            returnOriginal: false // gives us back updated arguemnt
        }).then((result) => {
        // log(result)
    });

})

//go to user profile
app.get('/user_profile', authenticateUser, (req, res) => {
    if (req.user) {
        req.session.user = req.user._id;
        log(req.user)
        log(req.session.user)
        res.sendFile(__dirname + '/public/user_profile.html')
    } else {
        res.redirect('/')
    }
})

//user signup
app.post('/users', (req, res) => {
    // Add code here

    const user = new User({
        name: req.body.name,
        email: req.body.email,
        password: req.body.password,
        posts: req.body.posts,
        reported: req.body.reported
        //creator: req.user._id // from the authenticate middleware

    })

    user.save().then((result) => {
        res.send(result)
    }, (error) => {
        res.status(400).send(error) // 400 for bad request
    })
})

//admin sign up
app.post('/admins', (req, res) => {
    // Add code here

    const admin = new Admin({
        name: req.body.name,
        email: req.body.email,
        password: req.body.password
        //creator: req.user._id // from the authenticate middleware
    })

    admin.save().then((result) => {
        res.send(result)
    }, (error) => {
        res.status(400).send(error) // 400 for bad request
    })
})

app.patch('/updateEmail', (req, res) => {

    User.findOneAndUpdate({
        "_id": new ObjectID(req.session.user), "email": req.body.oldEmail
    }, {
        // update operators: $set and $inc
        $set: {email: req.body.newEmail}
    }, {
        returnOriginal: false // gives us back updated arguemnt
    }).then((result) => {
        if (result == null) {
            res.status(404).send();
        }
        else {
            res.status(200).send()
        }
    });

})


// app.patch('/updatePassword', async (req, res) => {

//     let currUser = {};
//     await User.find({'postContent': req.body.oldpost.postContent, 'category': req.body.oldpost.category}).then(user => {
//         if (user) {
//             currUser = user;
//         }
//     }, (error) => {
//         log(error)
//     })
//     User.methods.comparePassword = {()

//     }
//     User.findOneAndUpdate({
//      "_id": new ObjectID(req.session.user)
//     }, {
//         // update operators: $set and $inc
//         $set: {email: req.body.newEmail}
//     }, {
//         returnOriginal: false // gives us back updated arguemnt
//     }).then((result) => {
//         if (result == null) {
//             res.status(404).send();
//         }
//         else {
//             res.status(200).send()
//         }
//     });
// })


app.listen(port, () => {
    log(`Listening on port ${port}...`)
});
