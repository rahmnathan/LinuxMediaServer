package nr.localmovies.restserver;

import nr.localmovies.exception.EmptyDirectoryException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestListenerTest {

    @Mock
    MovieInfoControl movieInfoControl;
    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest request;

    @InjectMocks
    RestListener restListener = new RestListener(movieInfoControl, null);

    @Test
    public void titleRequestTest() throws Exception {
        when(movieInfoControl.listMovies("TestPath")).thenThrow(new EmptyDirectoryException());
        when(request.getRemoteAddr()).thenReturn("LocalHost");
        Assert.assertEquals("Path must contain 'LocalMedia' directory and not be empty",
                restListener.titleRequest("TestPath", request, response).get(0).getTitle());
    }
}
