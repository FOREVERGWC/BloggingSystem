import {defineStore} from 'pinia'
import {getByToken, login, logout} from "@/api/auth.js";
import _ from "lodash-es";
import {ElMessage} from "element-plus";

const useUserStore = defineStore('user', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user') || '{}'),
        id: '',
        username: '',
        nickname: '',
        name: '',
        avatar: '',
        gender: '',
        birthday: '',
        status: '',
        phone: '',
        email: '',
        openId: '',
        loginIp: '',
        loginTime: '',
        token: '',
        roleIdList: [],
    }),
    getters: {
        getUser: (state) => {
            return state.user
        },
        isAdmin: (state) => {
            return state.user?.roleIdList?.includes('1')
        }
    },
    actions: {
        setUser(user) {
            this.user = user
        },
        handleLogin(data) {
            return new Promise((resolve, reject) => {
                login(data).then(res => {
                    if (res.code !== 200) {
                        ElMessage.error(res.msg)
                        return
                    }
                    ElMessage.success('登录成功！')
                    this.token = res.data
                    resolve()
                })
            })
        },
        getInfo() {
            return new Promise((resolve, reject) => {
                getByToken().then(res => {
                    const user = res.data
                    const roleIdList = user.roleList.map(item => item.id);
                    this.id = user.id
                    this.username = user.username
                    this.nickname = user.nickname
                    this.name = user.name
                    this.avatar = user.avatar
                    this.token = user.token
                    this.roleIdList = _.isEmpty(roleIdList) ? [] : roleIdList
                    resolve(res)
                }).catch(() => {
                    this.token = ''
                    reject()
                })
            })
        },
        handleLogout() {
            return new Promise((resolve, reject) => {
                logout().then(() => {
                    this.id = ''
                    this.username = ''
                    this.nickname = ''
                    this.name = ''
                    this.token = ''
                    this.roleIdList = []
                    resolve()
                }).catch(error => {
                    reject(error)
                })
            })
        }
    },
    persist: true
})

export default useUserStore
