 package com.app.ev119.domain.entity;

 import com.app.ev119.domain.type.SocialType;
 import jakarta.persistence.*;
 import lombok.*;

 @Getter @Setter
 @ToString(exclude = {
         "member"
 })
 @EqualsAndHashCode(onlyExplicitlyIncluded = true)
 @NoArgsConstructor @AllArgsConstructor
 @Entity
 @Table(name = "TBL_MEMBER_SOCIAL")
 @SequenceGenerator(
         name = "SEQ_MEMBER_SOCIAL_GENERATOR",
         sequenceName = "SEQ_MEMBER_SOCIAL",
         allocationSize = 1
 )
 public class MemberSocial {

     @Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MEMBER_SOCIAL_GENERATOR")
     @EqualsAndHashCode.Include
     private Long id;
     private String memberSocialProviderId;

     @Enumerated(EnumType.STRING)
     private SocialType memberSocialProvider;

     @ManyToOne(fetch =  FetchType.LAZY)
     @JoinColumn(name = "MEMBER_ID")
     private Member member;

 }
