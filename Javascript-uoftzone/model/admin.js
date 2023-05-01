const mongoose = require('mongoose');
const bcrypt = require('bcryptjs')


const AdminSchema = new mongoose.Schema({
	name: String,
	email: String,
	password: String,

},  { collection : 'Admin' });


// Our own student finding function
AdminSchema.statics.findByNamePassword = function(name, password) {
    const Admin = this

    return Admin.findOne({'name': name}).then((admin) => {
        if (!admin) {
            console.log('admin does not exist :(')
            return Promise.reject()
        }

        return new Promise((resolve, reject) => {
            console.log(admin)
            console.log('user pswd is ' + admin.password)
            console.log('pswd is ' + password)

            // if(user.password === password ){
            //             console.log('return the user')
            //             resolve(user);
            // } else {
            //             reject();
            //             console.log('pswd  dont match')
            // }
            bcrypt.compare(password, admin.password, (error, result) => {
                if (result) {
                    console.log('return the user')
                    resolve(admin);
                } else {
                    reject();
                    console.log('pswd  dont match')
                }
            })
        })
    })
}

// This function runs before saving user to database
AdminSchema.pre('save', function(next) {
    const admin = this

    if (admin.isModified('password')) {
        bcrypt.genSalt(10, (error, salt) => {
            bcrypt.hash(admin.password, salt, (error, hash) => {
                admin.password = hash
                next()
            })
        })
    } else {
        next();
    }

})
const Admin = mongoose.model('Admin', AdminSchema);

module.exports = { Admin };
