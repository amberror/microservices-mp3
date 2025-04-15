package example.services.impl;

import example.constants.SongConstants;
import example.exceptions.InvalidArgumentException;
import example.services.ConstraintsService;
import org.springframework.stereotype.Service;


@Service
public class ConstraintsServiceImpl implements ConstraintsService {

	@Override
	public void checkIdConstraints(Long id) {
		if(id < 1) {
			throw new InvalidArgumentException(String.format(SongConstants.INVALID_ID_VALUE_MESSAGE_TEMPLATE, id));
		}
	}

	@Override
	public void checkInlineStringIdsConstraints(String ids) {
		if(ids.length() > 200) {
			throw new InvalidArgumentException(String.format(SongConstants.CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE, ids.length()));
		}
	}


}
