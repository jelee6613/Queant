<template>
  <Navbar/>
  <!-- ======= Hero Section ======= -->
  <section id="hero" class="d-flex justify-cntent-center align-items-center">
    <div id="heroCarousel" class="container carousel carousel-fade" data-bs-ride="carousel" data-bs-interval="5000">

      <!-- Slide 1 -->
      <div class="carousel-item active">
        <div class="carousel-container">
          <h2 class="animate__animated animate__fadeInDown">Welcome to QueÆnt</h2>
          <p class="animate__animated animate__fadeInUp">Save Your Money, Safely.</p>
        </div>
      </div>

      <!-- Slide 2 -->
      <div class="carousel-item">
        <div class="carousel-container">
          <h2 class="animate__animated animate__fadeInDown">예금, 적금 상품</h2>
          <p class="animate__animated animate__fadeInUp">당신의 돈을 안전하고, 확실하게 관리해드립니다.</p>
          <router-link :to="{ name: 'productRecommend' }" class="btn-get-started animate__animated animate__fadeInUp">상품검색 바로가기</router-link>
        </div>
      </div>

      <!-- Slide 3 -->
      <div class="carousel-item">
        <div class="carousel-container">
          <h2 class="animate__animated animate__fadeInDown">포트폴리오</h2>
          <p class="animate__animated animate__fadeInUp">한 눈에 자산 정보를 확인하고, 계획을 세워보세요.</p>
          <router-link :to="{ name: 'portfolio' }" class="btn-get-started animate__animated animate__fadeInUp">포트폴리오 바로가기</router-link>
        </div>
      </div>

      <a class="carousel-control-prev" href="#heroCarousel" role="button" data-bs-slide="prev">
        <span class="carousel-control-prev-icon bx bx-chevron-left" aria-hidden="true"></span>
      </a>

      <a class="carousel-control-next" href="#heroCarousel" role="button" data-bs-slide="next">
        <span class="carousel-control-next-icon bx bx-chevron-right" aria-hidden="true"></span>
      </a>

    </div>
  </section><!-- End Hero -->
<br><br><br><br><br><br>
<main id="main">

    <!-- ======= Services Section ======= -->
    <section class="services">
      <div class="section-title">
        <h2>서비스 소개</h2>
        <p>QueÆnt가 제공해드리는 대표 서비스입니다.</p>
      </div>
      <div class="container">

        <div class="row">
          <div class="col-md-6 col-lg-3 d-flex align-items-stretch service-card" data-aos="fade-up">
            <div class="icon-box icon-box-pink">
              <div class="icon"><i class="fa-solid fa-building-columns"></i></div>
              <h4 class="title"><router-link style="text-decoration: none;" :to="{ name : 'bankInfoList' }">은행 정보</router-link></h4>
              <p class="description">은행 별 예금, 적금 상품 정보를 보고 상품을 선별할 수 있습니다.</p>
            </div>
          </div>

          <div class="col-md-6 col-lg-3 d-flex align-items-stretch service-card" data-aos="fade-up" data-aos-delay="100">
            <div class="icon-box icon-box-cyan">
              <div class="icon"><i class="fa-solid fa-thumbs-up"></i></div>
              <h4 class="title"><router-link style="text-decoration: none;" :to="{ name : 'productRecommend' }">상품 추천</router-link></h4>
              <p class="description">조건을 설정해 그에 맞는 예금, 적금 상품을 추천받을 수 있습니다.</p>
            </div>
          </div>

          <div class="col-md-6 col-lg-3 d-flex align-items-stretch service-card" data-aos="fade-up" data-aos-delay="200">
            <div class="icon-box icon-box-green">
              <div class="icon"><i class="fa-solid fa-cart-arrow-down"></i></div>
              <h4 class="title"><router-link style="text-decoration: none;" :to="{ name : 'portfolio' }">포트폴리오</router-link></h4>
              <p class="description">상품을 모아 자신만의 예금, 적금 상품 포트폴리오를 만들 수 있습니다.</p>
            </div>
          </div>

          <div class="col-md-6 col-lg-3 d-flex align-items-stretch service-card" data-aos="fade-up" data-aos-delay="200">
            <div class="icon-box icon-box-blue">
              <div class="icon"><i class="fa-solid fa-newspaper"></i></div>
              <h4 class="title"><router-link style="text-decoration: none;" :to="{ name : 'contents' }">컨텐츠</router-link></h4>
              <p class="description">QueÆnt팀에서 엄선해 제작한 다양한 금융 컨텐츠를 즐길 수 있습니다.</p>
            </div>
          </div>
        </div>
      </div>
    </section><!-- End Services Section -->
<br><br><br><br><br><br>
    <!-- ======= Features Section ======= -->
    <section class="features">
      <div class="container news-section">

        <div class="section-title" style="margin-bottom:10%;">
          <h2>News</h2>
          <p>투자에 도움이 되는 금융뉴스입니다.</p>
        </div>

        <HomeComponents
          v-for="news in newslist.slice(0, 4)"
          :key="news"
          :news=news>
        </HomeComponents>

      </div>
    </section>
  </main><!-- End #main -->
</template>

<script>
import Navbar from '@/components/Navbar.vue'
import HomeComponents from '@/components/HomeComponents.vue'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'HomeView',
  components : {
    Navbar : Navbar,
    HomeComponents : HomeComponents
  },
  computed: {
    ...mapGetters(['newslist'])
  },
  methods: {
    ...mapActions(['getNews'])
  },
  created() {
    this.getNews()
  },
  beforeCreate: function() {
    document.body.className = 'home_body'
  }
}
</script>

<style scoped>
@font-face {
    font-family: 'LeferiBaseType-RegularA';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2201-2@1.0/LeferiBaseType-RegularA.woff') format('woff');
    font-weight: normal;
    font-style: normal;
}

* {
  font-family: LeferiBaseType-RegularA;
}
</style>