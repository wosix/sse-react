import React from "react";
import { Container } from "@mui/system";
import SSEEvents from "./ui/SSEEvents";
import { SnackbarProvider } from 'notistack';
import Slide from "@mui/material/Slide";

function SlideTransition(props) {
    return <Slide {...props} direction="left" />;
}

const notistackAnchor = {
    vertical: 'bottom',
    horizontal: 'right'
}

const App = () => {
    return <Container sx={{
        height: '100vh'
    }}>
        <SnackbarProvider
            TransitionComponent={SlideTransition}
            maxSnack={7}
            anchorOrigin={notistackAnchor}
        >
            <SSEEvents />
        </SnackbarProvider>
    </Container>
}

export default App;