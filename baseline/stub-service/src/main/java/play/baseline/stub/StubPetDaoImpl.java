package play.baseline.stub;

import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PathVariable;
import play.baseline.BaselineService;
import play.baseline.dao.PetDao;
import play.baseline.dao.PetDaoImpl;
import play.baseline.model.Pet;
import play.remotemock.annotation.Remotable;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Remotable(PetDao.class)
public class StubPetDaoImpl extends PetDaoImpl {

    public StubPetDaoImpl(DataSource dataSource) {
        super(dataSource);
    }
}
