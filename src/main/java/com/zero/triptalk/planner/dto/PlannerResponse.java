package com.zero.triptalk.planner.dto;

import com.zero.triptalk.planner.entity.Planner;
import com.zero.triptalk.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannerResponse {

    private Long plannerId;
    private String description;
    private String title;
    private Integer likeCount;
    private Long views;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String nickname;
    private String profile;
    private Long userId;
    private List<PlannerDetailResponse> plannerDetailResponse;

    public static PlannerResponse of(Planner planner, UserEntity user, List<PlannerDetailResponse> details){

        return PlannerResponse.builder()
                .plannerId(planner.getPlannerId())
                .title(planner.getTitle())
                .description(planner.getDescription())
                .likeCount(planner.getLikes())
                .views(planner.getViews())
                .startDate(planner.getStartDate())
                .endDate(planner.getEndDate())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .userId(user.getUserId())
                .plannerDetailResponse(details)
                .build();
    }
}