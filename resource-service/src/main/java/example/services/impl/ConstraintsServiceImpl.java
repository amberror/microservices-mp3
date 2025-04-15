package example.services.impl;

import example.constants.ResourceConstants;
import example.exceptions.InvalidArgumentException;
import example.services.ConstraintsService;
import org.springframework.stereotype.Service;


@Service
public class ConstraintsServiceImpl implements ConstraintsService {

	@Override
	public void checkIdConstraints(Long id) {
		if(id < 1) {
			throw new InvalidArgumentException(String.format(ResourceConstants.INVALID_ID_VALUE_MESSAGE_TEMPLATE, id));
		}
	}

	@Override
	public void checkInlineStringIdsConstraints(String ids) {
		if(ids.length() > 200) {
			throw new InvalidArgumentException(String.format(ResourceConstants.CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE, ids.length()));
		}
	}
}
