<template>
  <Navbar />
  <header id="title-div">
    <h1 class="title" id="title">상품저장소</h1>
  </header>
  <!-- 장바구니 섹션 -->
	<div v-if="this.cartVisible" class="cart-none">
		<img
			src="../../assets/image/물음표개미_none.png"
			alt="없음"
			style="width: 240px; height: 240px"
		/>
		<br><br><br>
	</div>
  <section class="product_section">
    
		<div>
			<!-- 장바구니에 상품이 비어 있을 때 -->
			<div v-if="this.cartVisible" class="cart-none">
				<br /><br />
				<h3><strong>담긴 상품이 없습니다.</strong></h3>
				<br />
				<h6>
					상품을
					<router-link
						:to="{ name: 'productRecommend' }"
						style="text-decoration-line: none"
						>추천</router-link
					>
					받거나, 상단의 검색창을 이용해 추가해보세요.
				</h6><br><br>
			</div>
			<div>
				<div class="d-flex justify-content-center" style="width:60vw;">
					<div v-if="!this.cartVisible">
						<button class="btn btn-outline-danger btn-sm" @click="clearCart()">
						장바구니 전체 비우기 <i class="fa-solid fa-circle-minus fa-lg"></i>
						</button>
						<button
							class="btn btn-outline-primary btn-sm"
							@click="addComparisonPortfolio()"
						>
							가상 포트폴리오 추가 <i class="fa-solid fa-circle-plus fa-lg"></i>
						</button>
					</div>
					<div>
						<button
							class="btn btn-outline-success btn-sm"
							v-show="isLoggedIn"
							@click="saveToDb()"
						>
							가상 포트폴리오 최종 저장
						</button>
						<router-link
							:to="{ name: 'productComparison' }"
							class="btn btn-outline-success btn-sm"
							>가상 포트폴리오 비교하기</router-link
						>
					</div>
					<br><br><br>
				</div>
				<div>
					<table class="table table--block" cellspacing="0" cellpadding="0" v-if="!this.cartVisible">
						<thead>
							<tr class="text-center">
								<th>은행</th>
								<th>상품명</th>
								<th>금리</th>
								<th>기간</th>
								<th>비교 포트폴리오</th>
								<th>내 포트폴리오</th>
								<th>삭제</th>
							</tr>
						</thead>
						<tbody
							class="border"
							v-for="productInCart in cart"
							:key="productInCart.product.product_id"
						>
							<td>
								<router-link
									:to="{
										name: 'bankInfoDetail',
										params: { bankId: productInCart.product.bank_id },
									}"
									><img
										:src="productInCart.product.picture"
										alt=""
										style="width: 2rem"
								/></router-link>
							</td>
							<td>
								<router-link
									style="text-decoration-line: none"
									:to="{
										name: 'productDetail',
										params: { productId: productInCart.product.product_id },
									}"
									>{{ productInCart.product.name }}</router-link
								>
							</td>
							<td>
								{{
									(productInCart.applied_rate + productInCart.special_rate).toFixed(2)
								}}%
							</td>
							<td>{{ productInCart.applied_period }}개월</td>
							<td class="flex-wrap">
								<button
									v-for="cportfolio in comparisonPortfolio"
									:key="cportfolio.cportfolio_cnt"
									class="btn btn-outline-primary btn-sm p-1"
									style="height: 2rem; font-size: 0.8rem"
									@click="
										pushProductToCportfolio([
											cportfolio.cportfolio_cnt,
											productInCart,
										])
									"
								>
									{{ cportfolio.cportfolio_cnt }}번
									<i class="fa-solid fa-circle-plus"></i>
								</button>
							</td>
							<td>
								<button
									class="btn btn-outline-success btn-sm p-1"
									style="height: 2rem"
									@click="pushProductToPortfolio(productInCart)"
								>
									<i class="fa-solid fa-circle-plus fa-lg"></i>
								</button>
							</td>
							<td>
								<button
									class="btn btn-outline-danger btn-sm p-1"
									style="height: 2rem"
									@click="popProductFromCart(productInCart)"
								>
									<i class="fa-solid fa-circle-minus fa-lg"></i>
								</button>
							</td>
						</tbody>
					</table>
				</div>
			
			<!-- 장바구니에 상품이 담겨 있을 때 -->
			</div>
		</div>
  </section>
  <!-- 가상 포트폴리오 섹션 -->
  <section class="product-detail-box">
		<div>
      <div class="d-flex justify-content-center mt-3">
				<h1 style="font-family: jua">가상 포트폴리오 상품 관리</h1>
			</div>
			<div v-if="!this.cartVisible && this.portVisible" class="cart-none">
				<img
					src="../../assets/image/물음표개미_none.png"
					alt="없음"
					style="width: 240px; height: 240px"
				/>
				<br><br><br>
			</div>
			<div v-if="comparisonPortfolio.length === 0" class="cart-none">
      <br /><br />
      <h3><strong>가상 포트폴리오가 없습니다.</strong></h3>
      <br /><br />
			</div>

			<div v-else>
				<div
					class="product-detail d-flex flex-wrap justify-content-center"
					style="margin-top: -30px"
				>
					<div
						class="m-2 p-4 border border-1 my-5"
						v-for="cportfolio in comparisonPortfolio"
						id="cportfolio"
						:key="cportfolio"
						style="position: relative; height: 500px"
					>
						<button
							class="btn btn-danger"
							type="button"
							style="position: absolute; width: 100%; bottom: -40px; right: -3px"
							@click="deleteCportfolio(cportfolio.cportfolio_cnt)"
						>
							포트폴리오 삭제
						</button>
						<div
							class="border p-2"
							style="width: 100%; background-color: #92ce95; font-family: 'jua'"
						>
							<h5 class="text-center m-0">
								예상 포트폴리오 {{ cportfolio.cportfolio_cnt }}
							</h5>
						</div>
						<div class="my-2 mx-2" v-if="cportfolio.products.length === 0">
							<div>아직 상품이 없습니다.</div>
						</div>
						<div
							v-else
							v-for="cproduct in cportfolio.products"
							class="d-flex mx-2 my-2"
							style="font-size: 15px"
							:key="cproduct"
						>
							{{ cproduct.product.name }}
							<button
								style="height: 1.2rem; font-size: 5px"
								class="d-flex align-items-center p-2 btn btn-outline-danger btn-sm"
								@click="
									popProductFromCPortfolio([cportfolio.cportfolio_cnt, cproduct])
								"
							>
								상품삭제
							</button>
						</div>
					</div>
				</div>
    </div>
    </div>
  </section>
</template>

<script>
import Navbar from "@/components/Navbar.vue";
import { mapActions, mapGetters, mapMutations } from "vuex";
import draggable from "vuedraggable";
import router from '@/router';

export default {
  name: "ProductCartView",
  components: { Navbar, draggable },
  computed: {
    ...mapGetters([
      "isLoggedIn",
      "userInfo",
      "cart",
      "portfolios",
      "comparisonPortfolio",
      "newlyAddedPortfolio",
      "deletedPortfolio",
      "products",
      "comparisonProducts",
    ]),
  },
  methods: {
    ...mapActions([
      "fetchProduct",
      "saveToDb",
      "getFromDb",
      "pushProductToPortfolio",
    ]),
    ...mapMutations([
      "POP_PRODUCT_FROM_CPORTFOLIO",
      "CLEAR_CPORTFOLIO_DB",
      "CLEAR_CART",
      "POP_PRODUCT_FROM_CART",
      "PUSH_PRODUCT_TO_PORTFOLIO",
      "ADD_COMPARISON_PORTFOLIO",
      "CLEAR_CPORTFOLIOS",
      "PUSH_PRODUCT_TO_CPORTFOLIO",
      "POP_CPORTFOLIO",
      "UPDATE_CPORTFOLIO_FROM_DB",
    ]),

    popProductFromCPortfolio(value) {
      this.POP_PRODUCT_FROM_CPORTFOLIO(value);
    },
    clearDB() {
      this.CLEAR_CPORTFOLIO_DB();
    },
    clearCart() {
      this.CLEAR_CART();
    },
    deleteCportfolio(cportfolio_cnt) {
      this.POP_CPORTFOLIO(cportfolio_cnt);
    },
    popProductFromCart(product) {
      this.POP_PRODUCT_FROM_CART(product);
    },
    addComparisonPortfolio() {
			if (!this.isLoggedIn) {
				if (confirm('로그인이 필요합니다. 로그인 하시겠어요?') === true) {
					router.push({ name: 'login' })
				} else {
					return
				}
			}
      this.ADD_COMPARISON_PORTFOLIO();
    },
    clearcomparisonportfolio() {
      this.CLEAR_CPORTFOLIOS();
    },
    pushProductToCportfolio(value) {
      this.PUSH_PRODUCT_TO_CPORTFOLIO(value);
    },
  },
  mounted() {
    this.getFromDb();
  },
  beforeCreate: function () {
    document.body.className = "home_body";
  },
	created () {
		if (this.cart.length === 0) {
			this.cartVisible = true;
		} else if (this.comparisonPortfolio.length === 0) {
			this.portVisible = true;
		}
	},
	updated () {
		if (this.cart.length === 0) {
			this.cartVisible = true;
		} else {
			this.cartVisible = false;
		}

		if (this.comparisonPortfolio.length === 0) {
			this.portVisible = true;
		} else {
			this.portVisible = false;
		}
	},
	data () {
		return {
			cartVisible: false,
			portVisible: false,
		}
	}
};
</script>

<style scoped>
@import "../../assets/css/home.css";
@import "../../assets/css/product.css";
</style>
