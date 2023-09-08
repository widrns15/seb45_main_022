package com.codestatus.domain.hashTag.service;

import com.codestatus.domain.feed.entity.Feed;
import com.codestatus.domain.hashTag.command.FeedHashTagCommand;
import com.codestatus.domain.hashTag.entity.FeedHashTag;
import com.codestatus.domain.hashTag.entity.HashTag;
import com.codestatus.domain.hashTag.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final HashTagRepository hashTagRepository;

    private final FeedHashTagCommand feedHashTagCommand;


    //해쉬태그 생성
    @Override
    public void createEntity(HashTag hashTag) {
        hashTagRepository.save(hashTag);
    }

    //피드와 해시태그 만들고 연결해주는 메서드
    @Transactional
    public void createEntityByString(Feed feed, List<String> hashTags) {
        List<FeedHashTag> feedHashTags = new ArrayList<>();  //기존에 없던 HashTag를 넣어주기 위해 임시 리스트 생성
        for (String hashTagStr : hashTags) {  //리스트 내의 해쉬태그를 하나씩 확인
            HashTag hashTag = findOrCreateHashTag(hashTagStr); //하나씩 받아서 객체로 생성
            System.out.println("여기요~! : "+hashTag.getBody());
            FeedHashTag feedHashTag = new FeedHashTag(feed, hashTag); //피드해시태그로 만들어 매핑
            feedHashTags.add(feedHashTag); //생성한 해쉬태그를 임시 리스트에 추가
        }
        feedHashTagCommand.createFeedHashtags(feedHashTags); //해쉬태그 리스트를 리포지토리에 저장
    }

    //createEntityByString에서 쓰는 메서드
    //String을 받아 해시태그 검색해보고 있으면 있는거 반환, 없으면 만들어서 반환.
    private HashTag findOrCreateHashTag(String hashTagStr) {
        Optional<HashTag> optionalHashTag = Optional.ofNullable(findByString(hashTagStr));

        if (optionalHashTag.isPresent()) {
            return optionalHashTag.get(); // 이미 존재하는 해시태그 반환
        } else {
            HashTag newHashTag = new HashTag();
            newHashTag.setBody(hashTagStr);
            newHashTag.setDeleted(false);

            hashTagRepository.save(newHashTag); // 새로운 해시태그 저장
            return newHashTag;
        }
    }

    @Transactional(readOnly = true)
    public Page<HashTag> findHashTagByBody(String text, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "body"); //바디를 가나다순 정렬
        Pageable pageable = PageRequest.of(page, size, sort);
        return hashTagRepository.findHashTagByBodyLike(text, pageable);
    }

    public HashTag findByString(String hashTag) {
        Optional<HashTag> optionalHashTag = hashTagRepository.findHashTagByBody(hashTag);

        return optionalHashTag.orElse(null);
    }

    @Override
    public void updateEntity(HashTag updateEntity, long userId) {

    }

    @Override
    public void deleteEntity(long entityId, long userId) {

    }
}

