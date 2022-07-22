const HOST = 'http://localhost/'

const MEMBER = 'member/'
const SOCIAL = 'social/'

export default {
  member: {
    register: () => HOST + MEMBER + 'register',
    login: () => HOST + MEMBER + 'login',
    info: () => HOST + MEMBER + 'info',
    emailcheck: () => HOST + MEMBER + 'emailcheck',
    emailverify: () => HOST + MEMBER + 'emailverify',
  },
  social: {
    google: () => HOST + SOCIAL + 'google',
    kakao: () => HOST + SOCIAL + 'kakao'
  }
}