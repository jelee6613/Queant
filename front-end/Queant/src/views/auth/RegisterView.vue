<template>
  <div class="wrapper d-flex justify-content-center align-items-center">
    <div class="container">
      <h1 style="font-family: NanumSquareRound; margin-top: -130px;">회원가입</h1>
      <form @submit.prevent="register(credentials)" class="form">
        <!-- 이름 -->
        <input
          style="font-family: NanumSquareRound;"
          v-model="credentials.name"
          type="text"
          placeholder="이름"
          autocomplete="off"
          required>

        <!-- 이메일 -->
        <input
          v-model="credentials.email"
          v-bind:class="{error : error.email, complete:!error.email&&credentials.email.length!==0}"
          type="text"
          placeholder="Email"
          autocomplete="off"
          required
          :readonly="emailCheckedStatus === 200">
        <div class="error-text" v-if="error.email" style="margin-bottom:0px;">{{error.email}}</div>

        <!-- 중복검사 성공전에 버튼 활성화 주석 -->
        <div v-if="emailCheckedStatus !== 200">
          <button type="button" class="mail-send" style="color: darkslategrey; font-family: NanumSquareRound;"  @click="emailCheck(credentials.email)">Email 중복검사</button>
          <!-- 중복검사 결과 409 => 중복 이메일 주석 -->
          <p v-if="emailCheckedStatus === 409" style="margin-bottom:0px; margin-top:1px" >이미 가입된 이메일입니다.</p>
        </div>
        <!-- 중복검사 결과 200 성공 => 인증 메일 발송 주석 -->
        <div v-else-if="emailCheckedStatus === 200">
          <button type="button" class="mail-send" style="color: darkslategrey; font-family: NanumSquareRound;" @click="emailCheck(credentials.email)">재전송</button>
          <br>
          {{ time }}
          <p style="margin-bottom:0; margin-top:1px;">이메일로 인증 번호가 전송되었습니다.</p>
          <!-- 인증번호 검사 주석 -->
          <input
            v-model="code"
            type="text"
            required>
          
          <!-- 인증 성공전에 초기상태 주석 -->
          <button type="button" style="font-family: NanumSquareRound;" v-if="emailVerifiedStatus !== 200" @click="emailVerify(code)">인증번호 검사</button>
          
          <!-- 409 인증코드 불일치 주석 -->
          <p v-if="emailVerifiedStatus === 409" style="margin-bottom:0px; margin-top:1px;">인증번호가 다릅니다.</p>

          <!-- 200 인증코드 일치 주석 -->
          <button disabled v-else-if="emailVerifiedStatus === 200" style="font-family: NanumSquareRound;">성공</button>

        </div>

        <!-- 비밀번호 -->
        <input
          class="mt-4"
          style="font-family: NanumSquareRound;"
          v-model="credentials.password1"
          v-bind:class="{error : error.password1, complete:!error.password1&&credentials.password1.length!==0}"
          type="password"
          placeholder="비밀번호"
          autocomplete="off"
          required>
        <div class="error-text" v-if="error.password1" style="margin-bottom:10px;">{{ error.password1 }}</div>
        <!-- 비밀번호 확인 -->
        <input
          style="font-family: NanumSquareRound;"
          v-model="credentials.password2"
          v-bind:class="{error : error.password2, complete:!error.password2&&credentials.password2.length!==0}"
          type="password"
          placeholder="비밀번호 확인"
          autocomplete="off"
          required>
        <div class="error-text" v-if="error.password2" style="margin-bottom:0px;">{{ error.password2 }}</div>

        <!-- 성별 mx-1 mb-2 -->
        <div class="d-flex justify-content-center">
          <button
            @click="setGender('Female')"
            type="button"
            :class="{ opacity: selectedFemale }"
            style="width: 122px;
            height: 49px;
            margin-right: 2.5px;
            margin-bottom: 2%;
            color: darkslategrey;
            font-family: NanumSquareRound;">
            여자
          </button>

          <button
            @click="setGender('Male')"
            type="button"
            :class="{ opacity: selectedMale }"
            style="width: 122px;
            height: 49px;
            margin-left: 2.5px;
            margin-bottom: 2%;
            color: darkslategrey;
            font-family: NanumSquareRound;">
            남자
          </button>
        </div>

        <!-- 생년월일 -->
        <input
          class="date_empty placeholder-date"
          data-placeholder="생일"
          v-model="credentials.birthdate"
          type="date"
          id="birthdate">

        <button v-if="isCheckedForm && emailCheckedStatus === 200 && emailVerifiedStatus === 200" style="color: darkslategrey;" type="submit">회원가입</button>

        <button v-else disabled style="color: darkslategrey; font-family: NanumSquareRound; opacity: 70%;">회원가입</button>
      </form>
    </div>
   
    <ul class="bg-bubbles">
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
    </ul>
  </div>   
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import * as EmailValidator from 'email-validator'
import PV from 'password-validator'

export default {
  name: 'RegisterView',
  computed: {
    ...mapGetters(['emailCheckedStatus', 'emailVerifiedStatus']),
    time() {
      //3항 연산자를 이용하여 10보다 작을 경우 0을 붙이도록 처리 하였다.
      var min = parseInt((this.timerCount%3600)/60) < 10 ? '0'+ parseInt((this.timerCount%3600)/60) : parseInt((this.timerCount%3600)/60)
      var sec = this.timerCount % 60 < 10 ? '0'+this.timerCount % 60 : this.timerCount % 60
  
      //연산한 값을 화면에 뿌려주는 코드
      return min+':' + sec
    }
  },
  data() {
    return {
      isCheckedForm: false,
      credentials: {
        name: '',
        email: '',
        password1: '',
        password2: '',
        birthdate: '',
        gender: ''
      },
      code: '',
      passwordSchema: new PV(),
      error: {
        email : '',
        password1 : '',
        password2 : ''
      },
      timerCount : 300,
      selectedMale: false,
      selectedFemale: false
    }
  },
  methods: {
    ...mapActions(['register', 'emailCheck', 'emailVerify']),
    ...mapMutations(['SET_EMAIL_CHECKED_STATUS', 'SET_EMAIL_VERIFIED_STATUS']),
    checkForm() {
      if (this.credentials.email.length > 0 && !EmailValidator.validate(this.credentials.email))
        this.error.email = '올바른 이메일 형식이 아닙니다.'
      else this.error.email = ''
      
      if (this.credentials.password1.length > 0 && !this.passwordSchema.validate(this.credentials.password1))
        this.error.password1 = '영문,숫자 포함 8 자리이상이어야 합니다.'
      else this.error.password1 = ''
    
      if (this.credentials.password1 !== this.credentials.password2 && this.credentials.password2.length > 0) 
        this.error.password2 = '비밀번호가 일치하지 않습니다.'
      else this.error.password2 = ''

      if (!this.error.email && !this.error.password1 && !this.error.password2 && this.credentials.name && this.credentials.email && this.credentials.password1 && this.credentials.password2)
        this.isCheckedForm = true
      else this.isCheckedForm = false
    },
    countDown() {
      if (this.timerCount > 0)
        this.timerCount--
    },
    setGender(gender) {
      if (gender === 'Male') {
        this.selectedMale = true
        this.selectedFemale = false
      } else {
        this.selectedMale = false
        this.selectedFemale = true
      }
      this.credentials.gender = gender
    }
  },
  watch: {
    credentials: {
      deep: true,
      handler() {
        this.checkForm()
      }
    },
    emailCheckedStatus: {
      handler() {
        this.timerCount = 300
        clearTimeout(this.timer)
        if (this.emailCheckedStatus === 200)
          this.timerCount--
      }
    },
    timerCount: {
      handler() {
        if (this.timerCount === 0)
          alert('인증번호가 만료되었습니다. 재발급 받아주세요.')
        else if (this.timerCount < 300)
          this.timer = setTimeout(this.countDown, 1000)
      }
    }
  },
  beforeCreate: function() {
    // document.body.className = 'auth'
  },
  created() {
    this.SET_EMAIL_CHECKED_STATUS('')
    this.SET_EMAIL_VERIFIED_STATUS('')
    this.component = this
    this.isCheckedForm = false
    this.passwordSchema
      .is()
      .min(8)
      .is()
      .max(100)
      .has()  
      .digits()
      .has()
      .letters();
  },
}
</script>

<style scoped>

.date_empty:before {
  content: attr(data-placeholder);
  width: calc(100%);
}

/* 레페리포인트 */
/* @font-face {
    font-family: 'LeferiPoint-BlackA';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2201-2@1.0/LeferiPoint-BlackA.woff') format('woff');
    font-weight: normal;
    font-style: normal;
} */

@font-face {
    font-family: 'LeferiBaseType-RegularA';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2201-2@1.0/LeferiBaseType-RegularA.woff') format('woff');
    font-weight: normal;
    font-style: normal;
}

* {
  font-family: LeferiBaseType-RegularA !important;
  /* font-family: LeferiPoint-BlackA; */
}

@import url(http://fonts.useso.com/css?family=Source+Sans+Pro:200,300);
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  font-weight: 300;
}

.opacity {
  opacity: 70%;
}

body {
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  font-weight: 300;
}
body ::-webkit-input-placeholder {
  /* WebKit browsers */
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  font-weight: 300;
}
body :-moz-placeholder {
  /* Mozilla Firefox 4 to 18 */
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  opacity: 1;
  font-weight: 300;
}
body ::-moz-placeholder {
  /* Mozilla Firefox 19+ */
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  opacity: 1;
  font-weight: 300;
}
body :-ms-input-placeholder {
  /* Internet Explorer 10+ */
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  font-weight: 300;
}
.wrapper {
  background: #50a3a2;
  background: -webkit-linear-gradient(top left, #50a3a2 0%, #53e3a6 100%);
  background: linear-gradient(to bottom right, #50a3a2 0%, #53e3a6 100%);
  opacity: 0.8;
  position: absolute;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.wrapper.form-success .container h1 {
  -webkit-transform: translateY(85px);
      -ms-transform: translateY(85px);
          transform: translateY(85px);
}
.container {
  max-width: 600px;
  margin: 0 auto;
  /* padding: 80px 0; */
  height: 400px;
  text-align: center;
}
.container h1 {
  font-size: 40px;
  -webkit-transition-duration: 1s;
          transition-duration: 1s;
  -webkit-transition-timing-function: ease-in-put;
          transition-timing-function: ease-in-put;
  font-weight: 200;
}
form {
  padding: 20px 0;
  position: relative;
  z-index: 2;
}
form input {
  -webkit-appearance: none;
     -moz-appearance: none;
          appearance: none;
  outline: 0;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background-color: rgba(255, 255, 255, 0.2);
  width: 250px;
  border-radius: 3px;
  padding: 10px 15px;
  margin: 0 auto 10px auto;
  display: block;
  text-align: center;
  font-size: 18px;
  color: white;
  -webkit-transition-duration: 0.25s;
          transition-duration: 0.25s;
  font-weight: 300;
}
form input:hover {
  background-color: rgba(255, 255, 255, 0.4);
}
form input:focus {
  background-color: white;
  width: 300px;
  color: #53e3a6;
}
form button {
  -webkit-appearance: none;
     -moz-appearance: none;
          appearance: none;
  outline: 0;
  background-color: white;
  border: 0;
  padding: 10px 15px;
  color: #53e3a6;
  border-radius: 3px;
  width: 250px;
  cursor: pointer;
  font-size: 18px;
  -webkit-transition-duration: 0.25s;
          transition-duration: 0.25s;
}
form button:hover {
  background-color: #f5f7f9;
}
.bg-bubbles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}
.bg-bubbles li {
  position: absolute;
  list-style: none;
  display: block;
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.15);
  bottom: -160px;
  -webkit-animation: square 25s infinite;
  animation: square 25s infinite;
  -webkit-transition-timing-function: linear;
  transition-timing-function: linear;
}
.bg-bubbles li:nth-child(1) {
  left: 10%;
}
.bg-bubbles li:nth-child(2) {
  left: 20%;
  width: 80px;
  height: 80px;
  -webkit-animation-delay: 2s;
          animation-delay: 2s;
  -webkit-animation-duration: 17s;
          animation-duration: 17s;
}
.bg-bubbles li:nth-child(3) {
  left: 25%;
  -webkit-animation-delay: 4s;
          animation-delay: 4s;
}
.bg-bubbles li:nth-child(4) {
  left: 40%;
  width: 60px;
  height: 60px;
  -webkit-animation-duration: 22s;
          animation-duration: 22s;
  background-color: rgba(255, 255, 255, 0.25);
}
.bg-bubbles li:nth-child(5) {
  left: 70%;
}
.bg-bubbles li:nth-child(6) {
  left: 80%;
  width: 120px;
  height: 120px;
  -webkit-animation-delay: 3s;
          animation-delay: 3s;
  background-color: rgba(255, 255, 255, 0.2);
}
.bg-bubbles li:nth-child(7) {
  left: 32%;
  width: 160px;
  height: 160px;
  -webkit-animation-delay: 7s;
          animation-delay: 7s;
}
.bg-bubbles li:nth-child(8) {
  left: 55%;
  width: 20px;
  height: 20px;
  -webkit-animation-delay: 15s;
          animation-delay: 15s;
  -webkit-animation-duration: 40s;
          animation-duration: 40s;
}
.bg-bubbles li:nth-child(9) {
  left: 25%;
  width: 10px;
  height: 10px;
  -webkit-animation-delay: 2s;
          animation-delay: 2s;
  -webkit-animation-duration: 40s;
          animation-duration: 40s;
  background-color: rgba(255, 255, 255, 0.3);
}
.bg-bubbles li:nth-child(10) {
  left: 90%;
  width: 160px;
  height: 160px;
  -webkit-animation-delay: 11s;
          animation-delay: 11s;
}
@-webkit-keyframes square {
  0% {
    -webkit-transform: translateY(0);
            transform: translateY(0);
  }
  100% {
    -webkit-transform: translateY(-700px) rotate(600deg);
            transform: translateY(-700px) rotate(600deg);
  }
}
@keyframes square {
  0% {
    -webkit-transform: translateY(0);
            transform: translateY(0);
  }
  100% {
    -webkit-transform: translateY(-700px) rotate(600deg);
            transform: translateY(-700px) rotate(600deg);
  }
}
</style>