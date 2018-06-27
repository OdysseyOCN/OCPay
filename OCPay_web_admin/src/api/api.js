import axios from 'axios';
import { version } from 'core-js';
let get='get'
let base =get+'/api/ocpay/v1/';
let baseUrl=get+'/api/ocpay/upload/token/v1/';
// 登陆
export const requestLogin = params => { return axios.post(`${base}/login`, params).then(res => res.data); };
// 创建用户
export const addLogin = params => { return axios.post(`${base}/add-admin`, params).then(res => res.data); };
// 修改密码
export const becamePass = params => { return axios.post(`${base}/admin-pwd`, params).then(res => res.data); };
//账户列表
export const AccountList = params => { return axios.get(`${base}/user/users`+params).then(res => res.data); };
// 用户反馈
export const FeedbackUser= params => { return axios.post(`${base}/token/get-feedback`, params).then(res => res.data); };



// version
export const versionList = params => { return axios.post(`${base}/get-version`,params).then(res => res.data); };
// 添加
export const versionAdd = params => { return axios.post(`${base}/add-version`,params).then(res => res.data); };
// 编辑
export const versionEdit = params => { return axios.post(`${base}/edit-version`,params).then(res => res.data); };
// 删除
export const versionDelete = params => { return axios.post(`${base}/delete-version`,params).then(res => res.data); };

// Homepage
export const Homepage = params => { return axios.post(`${base}/get-homePage`,params).then(res => res.data); };
// HomepageEdit
export const HomepageEdit = params => { return axios.post(`${base}/edit-homePage`,params).then(res => res.data); };
// HomepageDelete
export const HomepageDelete = params => { return axios.post(`${base}/delete-homePage`,params).then(res => res.data); };
// add
export const HomepageAdd = params => { return axios.post(`${base}/add-homePage`,params).then(res => res.data); };

// Banner
// Banner
export const Banner= params => { return axios.post(`${base}/get-advertisment`,params).then(res => res.data); };
// BannerEdit
export const BannerEdit= params => { return axios.post(`${base}/edit-advertisment`,params).then(res => res.data); };
// BannerDelete
export const BannerDelete= params => { return axios.post(`${base}/delete-advertisment`,params).then(res => res.data); };
// BannerAdd
export const BannerAdd= params => { return axios.post(`${base}/add-advertisment`,params).then(res => res.data); };
// get-hompageid
export const BannerGet= params => { return axios.post(`${base}/get-homePageId`,params).then(res => res.data); };




// 上传图片
export const upload= params => { return axios.post(`${baseUrl}/file`,params).then(res => res.data); };









