package org.battlemap.battlemapbe.service.impl;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.region.UserDongPointResponse;
import org.battlemap.battlemapbe.repository.UserQuestsRepository;
import org.battlemap.battlemapbe.service.PointService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserQuestsRepository userQuestsRepository;

    @Override
    public List<UserDongPointResponse> getMyDongPoints(String loginId, String cityName) {
        return userQuestsRepository.findDongPointsByLoginIdAndCity(loginId, cityName);
    }
}
