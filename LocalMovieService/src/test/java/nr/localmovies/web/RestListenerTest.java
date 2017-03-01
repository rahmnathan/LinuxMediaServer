package nr.localmovies.web;

import nr.localmovies.control.MovieInfoControl;
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
    private MovieInfoControl movieInfoControl;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private RestListener restListener;

    @Test
    public void titleRequestExceptionTest() throws Exception {
        when(movieInfoControl.listMovies("TestPath")).thenThrow(new EmptyDirectoryException());
        when(request.getRemoteAddr()).thenReturn("LocalHost");
        Assert.assertEquals("Path must contain 'LocalMedia' directory and not be empty",
                restListener.titleRequest("TestPath", request, response).get(0).getTitle());
    }
}
