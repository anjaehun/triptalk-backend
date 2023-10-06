package com.zero.triptalk.like.service;

import com.zero.triptalk.exception.code.LikeErrorCode;
import com.zero.triptalk.exception.code.UserErrorCode;
import com.zero.triptalk.exception.custom.LikeException;
import com.zero.triptalk.exception.custom.UserException;
import com.zero.triptalk.like.dto.response.LikenOnePlusMinusResponse;
import com.zero.triptalk.like.entity.DetailPlannerLike;
import com.zero.triptalk.like.entity.PlannerLike;
import com.zero.triptalk.like.entity.UserLikeEntity;
import com.zero.triptalk.like.repository.DetailPlannerLikeRepository;
import com.zero.triptalk.like.repository.PlannerLikeRepository;
import com.zero.triptalk.like.repository.UserLikeRepository;
import com.zero.triptalk.planner.entity.Planner;
import com.zero.triptalk.planner.entity.PlannerDetail;
import com.zero.triptalk.planner.repository.PlannerDetailRepository;
import com.zero.triptalk.planner.repository.PlannerRepository;
import com.zero.triptalk.user.entity.UserEntity;
import com.zero.triptalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.zero.triptalk.exception.code.LikeErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final PlannerDetailRepository plannerDetailRepository;
    private final DetailPlannerLikeRepository detailPlannerLikeRepository;
    private final UserRepository userRepository;
    private final UserLikeRepository userLikeRepository;

    private final PlannerLikeRepository plannerLikeRepository;

    /**
     * 토큰 값안의 이메일 불러오기
     * @return
     */
    public String userEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = "기본 이메일";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername(); // 사용자 이메일 정보를 추출
        }

        return email;
    }
    @Transactional
    public Object createLikeOrPlusPlannerDetail(Long plannerDetailId) {
        PlannerDetail plannerDetail = plannerDetailRepository.findById(plannerDetailId)
                .orElseThrow(() -> new LikeException(LikeErrorCode.NO_Planner_Detail_Board));

        // 좋아요를 한 유저 찾기
        String email = userEmail(); // 이메일 불러오기
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_NOT_FOUND_ERROR));

        // 이미 좋아요를 한 경우 처리
        if (userLikeRepository.existsByPlannerDetailAndUser(plannerDetail, userEntity)) {
            throw new LikeException(LikeErrorCode.NO_LIKE_DUPLICATE_ERROR);
        }

        // 좋아요  한 유저 저장
        UserLikeEntity userLike = UserLikeEntity.builder()
                .plannerDetail(plannerDetail)
                .planner(plannerDetail.getPlanner())
                .user(userEntity)
                .build();
        userLikeRepository.save(userLike);

        // DetailPlannerLike 업데이트 또는 생성
        DetailPlannerLike detailPlannerLike = detailPlannerLikeRepository.findByPlannerDetail(plannerDetail);
        PlannerLike plannerLike = plannerLikeRepository.findByPlanner(plannerDetail.getPlanner());
        if (detailPlannerLike == null) {
            // detailPlannerLike 추가
            detailPlannerLike = DetailPlannerLike.builder()
                    .plannerDetail(plannerDetail)
                    .likeCount(1.0)
                    .build();
            detailPlannerLikeRepository.save(detailPlannerLike);

        }

        if( plannerLike == null){
            plannerLike = PlannerLike.builder()
                    .planner(plannerDetail.getPlanner())
                    .likeCount(1.0)
                    .build();

            plannerLikeRepository.save(plannerLike);

            return LikenOnePlusMinusResponse.builder()
                    .ok("좋아요가 완료되었습니다")
                    .build();
        }
        // detailPlannerLike 추가
        double currentDetailLikeCount = detailPlannerLike.getLikeCount();
        double newDetailLikeCount = currentDetailLikeCount + 1;
        detailPlannerLike.setLikeCount(newDetailLikeCount);
        // plannerLike 추가
        double currentPlannerLikeCount = plannerLike.getLikeCount();
        double newPlannerLikeCount = currentPlannerLikeCount + 1;
        plannerLike.setLikeCount(newPlannerLikeCount);

        detailPlannerLikeRepository.save(detailPlannerLike);
        plannerLikeRepository.save(plannerLike);

        return LikenOnePlusMinusResponse.builder()
                .ok("좋아요가 완료되었습니다")
                .build();
    }

    public Object LikeOneMinus(Long plannerDetailId) {
        PlannerDetail plannerDetail = plannerDetailRepository.findById(plannerDetailId)
                .orElseThrow(() -> new LikeException(NO_Planner_Detail_Board));

        DetailPlannerLike detailPlannerLike = detailPlannerLikeRepository.findByPlannerDetail(plannerDetail);


        double currentLikeCount = detailPlannerLike.getLikeCount();
        double newLikeCount = currentLikeCount - 1;

        detailPlannerLike.setLikeCount(newLikeCount);
        detailPlannerLikeRepository.save(detailPlannerLike);

        return LikenOnePlusMinusResponse.builder()
                .ok("좋아요가 취소되었습니다")
                .build();
    }
}