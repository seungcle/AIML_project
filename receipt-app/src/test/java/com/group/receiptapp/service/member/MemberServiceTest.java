
package com.group.receiptapp.service.member;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupRepository groupRepository;

    @Test
    public void 회원가입() throws Exception {
        //Given
        Group group = new Group();
        group.setName("Test Group");
        groupRepository.save(group); // 그룹 저장

        Member member = new Member();
        member.setName("kim");
        member.setEmail("kim@example.com");
        member.setPassword("password123");
        member.setGroup(group);

        //When
        Member savedMember = memberService.join(member);
        Long saveId = savedMember.getId();

        //Then
        Member foundMember = memberRepository.findById(saveId).orElse(null);
        assertNotNull(foundMember);
        assertEquals(member.getName(), foundMember.getName());
    }
    @Test
    public void 중복_회원_예외() throws Exception {
        //Given
        Group group = new Group();
        group.setName("Test Group");
        groupRepository.save(group); // 그룹 저장

        Member member1 = new Member();
        member1.setName("kim");
        member1.setEmail("kim@example.com");
        member1.setPassword("password123");
        member1.setGroup(group);

        Member member2 = new Member();
        member2.setName("kim");
        member2.setEmail("kimm@example.com");
        member2.setPassword("password1223");
        member2.setGroup(group);

        //When
        memberService.join(member1);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

        assertEquals("이미 존재하는 회원입니다", exception.getMessage());
    }

}
