const mongoose = require('mongoose');
// const { User } = require('./user')

const PostSchema = new mongoose.Schema({
    likes: Number,
    reported: Number,
    replies: [this],
    poster: String,
    postContent: String,
    category: String
},  { collection : 'Post' });

const Post = mongoose.model('Post', PostSchema);


module.exports = { Post };