import React, { useEffect, useState } from "react";

import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Header from "./components/header/Header";
import Home from "./components/home/Home";
import MovieDetails from "./components/movie/details/MovieDetails";
import ShowDetails from "./components/show/details/ShowDetails";
import PageNotFound from "./components/pageNotFound/PageNotFound";
import Footer from "./components/footer/Footer";
import MoviesPage from "./components/movie/page/MoviesPage";
import ShowsPage from "./components/show/page/ShowsPage";
import Watchlist from "./components/watchlist/Watchlist";

import { GlobalProvider } from "./context/GlobalState";
import SearchResults from "./components/search/SearchResults";
import AuthRouteGuard from "./context/AuthRouteGuard";
import LoginPage from "./components/login/LoginPage";
import RegisterPage from "./components/register/RegisterPage";
import { Slide, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
  return (
    <GlobalProvider>
      <div className="min-h-screen">
        <ToastContainer
          position="top-center"
          autoClose={5000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="colored"
          transition={Slide}
        />
        <Router>
          <Header />
          <main>
            <Routes>
              <Route path="/" exact element={<Home />} />
              <Route path="/movies" exact element={<MoviesPage />} />
              <Route path="/shows" exact element={<ShowsPage />} />
              <Route
                path="/watchlist"
                exact
                element={
                  <AuthRouteGuard mustBeLoggedIn={true}>
                    <Watchlist />
                  </AuthRouteGuard>
                }
              />
              <Route
                path="/login"
                exact
                element={
                  <AuthRouteGuard mustBeLoggedIn={false}>
                    <LoginPage />
                  </AuthRouteGuard>
                }
              />
              <Route
                path="/register"
                exact
                element={
                  <AuthRouteGuard mustBeLoggedIn={false}>
                    <RegisterPage />
                  </AuthRouteGuard>
                }
              />
              <Route path="/movie/:id" element={<MovieDetails />} />
              <Route path="/show/:id" element={<ShowDetails />} />
              <Route path="/search/:title" element={<SearchResults />} />
              <Route path="/*" element={<PageNotFound />} />
            </Routes>
          </main>
          <Footer />
        </Router>
      </div>
    </GlobalProvider>
  );
}

export default App;
