package com.ssafy.queant.model.service.portfolio;

import com.ssafy.queant.model.dto.portfolio.PortfolioDto;
import com.ssafy.queant.model.dto.portfolio.PortfolioResponseDto;
import com.ssafy.queant.model.dto.product.ConditionsDto;
import com.ssafy.queant.model.dto.product.CustomProductDto;

import com.ssafy.queant.model.entity.member.Member;
import com.ssafy.queant.model.entity.portfolio.Portfolio;
import com.ssafy.queant.model.entity.product.Conditions;
import com.ssafy.queant.model.entity.product.CustomProduct;
import com.ssafy.queant.model.entity.product.Options;
import com.ssafy.queant.model.entity.product.Product;
import com.ssafy.queant.model.repository.MemberRepository;
import com.ssafy.queant.model.repository.portfolio.PortfolioRepository;
import com.ssafy.queant.model.repository.product.CustomProductRepository;
import com.ssafy.queant.model.repository.product.OptionsRepository;
import com.ssafy.queant.model.repository.product.ProductRepository;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

   private final CustomProductRepository customProductRepository;
   private final ModelMapper modelMapper;

   private final ProductRepository productRepository;

   private final MemberRepository memberRepository;

   private final PortfolioRepository portfolioRepository;

   private final OptionsRepository optionsRepository;

   @Autowired
   public PortfolioServiceImpl(
              CustomProductRepository customProductRepository,
              ModelMapper modelMapper,
              ProductRepository productRepository,
              MemberRepository memberRepository,
              PortfolioRepository portfolioRepository,
              OptionsRepository optionsRepository
           ) {
      this.customProductRepository = customProductRepository;
      this.modelMapper = modelMapper;
      this.productRepository = productRepository;
      this.memberRepository = memberRepository;
      this.portfolioRepository = portfolioRepository;
      this.optionsRepository = optionsRepository;
   }

   @Override
   @Transactional
   public CustomProductDto registCustomProduct(CustomProductDto customProductDto, UUID memberId) throws RuntimeException{

      // dto -> entity
      // ??????
      // ????????? ??? entity -> dto ????????? ??????
      log.info("[????????? ?????? ?????? ??????]");

      CustomProduct customProduct = modelMapper.map(customProductDto,CustomProduct.class);
      customProduct.setMemberId(memberId); //?????? ????????? ??????
      Date endDate = addMonth(customProductDto.getStartDate(), customProductDto.getSaveTerm());
      customProduct.setEndDate(endDate); //endDate ??????

      CustomProduct savedCustomProduct = customProductRepository.save(customProduct);

      CustomProductDto savedCustomProductDto = modelMapper.map(savedCustomProduct,CustomProductDto.class);

      return savedCustomProductDto;
   }

   @Override
   @Transactional
   public boolean deleteCustomProduct(CustomProductDto customProductDto) throws RuntimeException{
      log.info("[????????? ?????? ?????? ??????]");

      CustomProduct customProduct = modelMapper.map(customProductDto,CustomProduct.class);
      Optional<CustomProduct> result = customProductRepository.findByProductId(customProduct.getProductId());

      if(result.isPresent()){
         customProductRepository.delete(result.get());
         return true;
      }

      return false;
   }

   @Override
   @Transactional
   public List<CustomProductDto> findCustomProductByMemberId(UUID memberId) throws RuntimeException {
      log.info("[????????? ?????? ?????? ????????????");
      Optional<List<CustomProduct>> result = customProductRepository.findByMemberId(memberId);
      result.orElseThrow(() -> new NoSuchElementException());

      List<CustomProductDto> customProductDtoList = new ArrayList<>();
      for (CustomProduct p:result.get()) {
            customProductDtoList.add(modelMapper.map(p,CustomProductDto.class));
      }
      return customProductDtoList;
   }

   @Override
   @Transactional
   public CustomProductDto updateCustomProduct(CustomProductDto customProductDto) throws Exception {
      Optional<CustomProduct> result = customProductRepository.findByProductId(customProductDto.getProductId());
      result.orElseThrow(() -> new NoSuchElementException());

      CustomProduct customProduct = modelMapper.map(customProductDto, CustomProduct.class);

      customProduct.setMemberId(result.get().getMemberId());
      Date endDate = addMonth(customProductDto.getStartDate(), customProductDto.getSaveTerm());
      customProduct.setEndDate(endDate); //endDate ??????
      CustomProduct savedCustomProduct = customProductRepository.save(customProduct);

      CustomProductDto savedCustomProductDto = modelMapper.map(savedCustomProduct,CustomProductDto.class);

      return savedCustomProductDto;
   }

   @Override
   @Transactional
   public PortfolioResponseDto getMyPortfolio(UUID memberId) throws Exception {
      log.info("[getMyPortfolio] : memberId: {}", memberId);
      PortfolioResponseDto portfolioResponseDto = new PortfolioResponseDto();
      //????????? ???????????? ??????
      try{
         List<CustomProductDto> customProductDtoList = findCustomProductByMemberId(memberId);
         portfolioResponseDto.setCustomProductList(customProductDtoList);
      } catch(Exception e){
         e.printStackTrace();
      }

      //0??? ??????????????? ??????

      try{
        List<PortfolioDto> portfolioDtoList = getPortfolio(memberId,0);
        portfolioResponseDto.setPortfolioList(portfolioDtoList);
      } catch(Exception e){
         e.printStackTrace();
      }

      return portfolioResponseDto;
   }

   @Override
   @Transactional
   public List<PortfolioDto> getPortfolio(UUID memberId, int portfolioNo) throws Exception {
      List<PortfolioDto> response = new ArrayList<>();

      Member member = Member.builder().memberId(memberId).build();
      Optional<List<Portfolio>> result = portfolioRepository.findPortfolioByMemberAndPortfolioNo(member,portfolioNo);
      result.orElseThrow(() -> new NoSuchElementException("?????? ?????????????????? ????????????."));

      result.get().forEach(portfolio -> response.add(modelMapper.map(portfolio, PortfolioDto.class)));

      for(PortfolioDto portfolioDto : response){
         portfolioDto.addConditionIds();
      }
      return response;
   }

   @Override
   @Transactional
   public void insertPortfolio(UUID memberId, List<PortfolioDto> portfolioDtoList) throws Exception{

      log.info("[insertPortfolio] : memberId: {} ??????????????? ??????", memberId);
      int portfolioIdx = 0;
      for(PortfolioDto portfolioDto : portfolioDtoList){
         portfolioIdx = portfolioDto.getPortfolioNo();

         Product product = Product.builder().productId(portfolioDto.getProductId()).build();
         Member member = Member.builder().memberId(memberId).build();
         //????????? ???????????? endDate ?????????
         Optional<Options> result = optionsRepository.findById(portfolioDto.getOptionId());
         Options option = result.get();

         Date endDate = addMonth(portfolioDto.getStartDate(), option.getSaveTerm());
         //??????????????? ??????
         Portfolio portfolio = Portfolio.builder()
                 .member(member)
                 .product(product)
                 .portfolioNo(portfolioDto.getPortfolioNo())
                 .amount(portfolioDto.getAmount())
                 .startDate(portfolioDto.getStartDate())
                 .endDate(endDate)
                 .option(option)
                 .build();

         //????????? ?????? ??? ??????
         for(int conditionId : portfolioDto.getConditionIds()){
            Conditions condition = Conditions.builder().conditionId(conditionId).build();
            portfolio.addCondition(condition);
         }

         portfolioRepository.save(portfolio);
      }

      Member member = memberRepository.findById(memberId).get();
      member.setPortfolio_cnt(portfolioIdx);
      memberRepository.save(member);
   }

   public Date addMonth(Date date, int months) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.MONTH, months);
      return cal.getTime();
   }


   //??????????????? ??????(?????? ??????????????? ?????? ?????? ??? ??????)
//   @Override
//   @Transactional
//   public void updatePortfolio(UUID memberId, List<PortfolioDto> portfolioDtoList, int portfolioNo) throws Exception {
//      //????????? ??????????????? ????????????
//      Member member = Member.builder().memberId(memberId).build();
//      Optional<List<Portfolio>> portfolioResult = portfolioRepository.findPortfolioByMemberAndPortfolioNo(member,portfolioNo);
//      portfolioResult.orElseThrow(() -> new NoSuchElementException());
//
//      HashMap<Integer,Integer> map = new HashMap<>();
//      List<Portfolio> existing = portfolioResult.get();
//
//      for(int i=0; i<existing.size(); i++){
//         map.put(existing.get(i).getPortfolioId(),i);
//      }
//
//      for(PortfolioDto portfolioDto: portfolioDtoList){
//         //????????? ?????? ??????????????? ??????
//         if(map.containsKey(portfolioDto.getPortfolioId())){
//            //?????? -> portfolio ??????-> portfolioDto
//            Portfolio portfolio = existing.get(map.get(portfolioDto.getPortfolioId()));
//            portfolio.setAmount(portfolioDto.getAmount());
//            portfolio.setStartDate(portfolioDto.getStartDate());
//            portfolio.setEndDate(portfolioDto.getEndDate());
//
//            //?????? ??????
//            if(portfolio.getOption().getOptionId() != portfolioDto.getOptionId()){
//               portfolio.setOption(Options.builder().optionId(portfolioDto.getOptionId()).build());
//            }
//
//            //????????? ??????
//            HashSet<Integer> conds = new HashSet<>();
//            Set<Conditions> changeConditions = new HashSet<>();
//            for(Conditions conditions : portfolio.getConditions()){
//               conds.add(conditions.getConditionId());
//            }
//
//            for(int cond : portfolioDto.getConditionIds()){
//               //?????? ????????? ?????? set ?????? ??????
//               if(conds.contains(cond)){
//                  Conditions condition = Conditions.builder().conditionId(cond).build();
//                  changeConditions.add(condition);
//                  conds.remove(cond);
//               } else { //????????? ???????????????
//                  Conditions condition = Conditions.builder().conditionId(cond).build();
//                  changeConditions.add(condition);
//               }
//            }
//
//            portfolio.setConditions(changeConditions);
//
//            portfolioRepository.save(portfolio);
//            map.remove(portfolioDto.getPortfolioId());
//         } else { //????????? ????????? ??????
//            Product product = Product.builder().productId(portfolioDto.getProductId()).build();
//            Options option = Options.builder().optionId(portfolioDto.getOptionId()).build();
//
//            //??????????????? ??????
//            Portfolio portfolio = Portfolio.builder()
//                    .member(member)
//                    .product(product)
//                    .portfolioNo(portfolioDto.getPortfolioNo())
//                    .amount(portfolioDto.getAmount())
//                    .startDate(portfolioDto.getStartDate())
//                    .endDate(portfolioDto.getEndDate())
//                    .option(option)
//                    .build();
//
//            //????????? ?????? ??? ??????
//            for(int conditionId : portfolioDto.getConditionIds()){
//               Conditions condition = Conditions.builder().conditionId(conditionId).build();
//               portfolio.addCondition(condition);
//            }
//
//            portfolioRepository.save(portfolio);
//         }
//      }
//      //set??? ??????????????? ??????
//      for(int key : map.keySet()){
//         portfolioRepository.deleteById(key);
//      }
//   }

//   @Override
//   @Transactional
//   public void deletePortfolio(UUID memberId, int portfolioNo) throws Exception {
//
//      log.info("[updatePortfolio] : memberId: {} ??????????????? ??????, ??????????????? ??????: {}", memberId, portfolioNo);
//
//      Optional<Member> result = memberRepository.findById(memberId);
//      Member member = result.get();
//
//      Optional<List<Portfolio>> portfolioResult = portfolioRepository.findPortfolioByMemberAndPortfolioNo(member,portfolioNo);
//      portfolioResult.orElseThrow(()-> new NoSuchElementException());
//
//      portfolioRepository.deleteAll(portfolioResult.get());
//
//      for(int i=portfolioNo+1; i<member.getPortfolio_cnt(); i++){
//         Optional<List<Portfolio>> portfolios = portfolioRepository.findPortfolioByMemberAndPortfolioNo(member, i);
//
//         for(Portfolio portfolio : portfolios.get()){
//            portfolio.setPortfolioNo(i-1);
//            portfolioRepository.save(portfolio);
//         }
//      }
//
//      member.setPortfolio_cnt(member.getPortfolio_cnt()-1);
//
//   }

   @Override
   @Transactional
   public void updatePortfolioSingle(PortfolioDto portfolioDto) throws Exception {
      Optional<Portfolio> result = portfolioRepository.findById(portfolioDto.getPortfolioId());
      result.orElseThrow(() -> new NoSuchElementException());
      Portfolio portfolio = result.get();

      portfolio.setAmount(portfolioDto.getAmount());
      portfolio.setStartDate(portfolioDto.getStartDate());
      portfolio.setEndDate(portfolioDto.getEndDate());

      //?????? ??????
      if(portfolio.getOption().getOptionId() != portfolioDto.getOptionId()){
         portfolio.setOption(Options.builder().optionId(portfolioDto.getOptionId()).build());
      }

      //????????? ??????
      HashSet<Integer> conds = new HashSet<>();
      Set<Conditions> changeConditions = new HashSet<>();
      for(Conditions conditions : portfolio.getConditions()){
         conds.add(conditions.getConditionId());
      }

      for(int cond : portfolioDto.getConditionIds()){
         //?????? ????????? ?????? set ?????? ??????
         if(conds.contains(cond)){
            Conditions condition = Conditions.builder().conditionId(cond).build();
            changeConditions.add(condition);
            conds.remove(cond);
         } else { //????????? ???????????????
            Conditions condition = Conditions.builder().conditionId(cond).build();
            changeConditions.add(condition);
         }
      }

      portfolio.setConditions(changeConditions);

      portfolioRepository.save(portfolio);
   }

   @Override
   @Transactional
   public void deletePortfolioSingle(int portfolioId) throws Exception {
      Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
      portfolio.orElseThrow(() -> new NoSuchElementException());
      portfolioRepository.delete(portfolio.get());
   }

   @Override
   @Transactional
   public void deleteAndInsert(UUID memberId, List<PortfolioDto> portfolioDtoList) throws Exception {
      Member member = Member.builder().memberId(memberId).build();
      Optional<List<Portfolio>> portfolio = portfolioRepository.findPortfolioByMember(member);

      if(portfolio.isPresent()){
         for(Portfolio port: portfolio.get()){
            if(port.getPortfolioNo() == 0) continue;
            log.info("[deleteAdnInsert] ???????????? ??????????????? : {}", port.toString());
            portfolioRepository.delete(port);

         }
      }

      portfolioRepository.flush();

      insertPortfolio(memberId, portfolioDtoList);
   }

}
