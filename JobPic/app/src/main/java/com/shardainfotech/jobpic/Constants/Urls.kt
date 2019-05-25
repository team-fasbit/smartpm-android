package com.shardainfotech.jobpic.Constants


// Production
var BASE_URL: String = "http://rkjinfotech.com/jobpicsbackend/public/api/"

var PROFILE_IMAGE_BASE_URL: String = "http://rkjinfotech.com/jobpicsbackend/public/profilepics/"


// Development
//var BASE_URL: String = "http://10.0.2.2:8000/api/"



var REGISTER_URL = BASE_URL + "signup"
var LOGIN_URL = BASE_URL + "login"
var FORGOT_PASSWORD = BASE_URL + "forgotpassword"
var CHANGE_PASSWORD_URL = BASE_URL + "changepassword"
var LOGOUT_URL = BASE_URL + "logout"

var PROFILE_IMG_UPDATE__URL = BASE_URL + "updateprofilepic"

var POST_JOB = BASE_URL + "addeditjob"
var GET_Job = BASE_URL + "getalljobs?page="

const val IMAGE_BASE_URL = "http://rkjinfotech.com/jobpicsbackend/public/jobimages/"