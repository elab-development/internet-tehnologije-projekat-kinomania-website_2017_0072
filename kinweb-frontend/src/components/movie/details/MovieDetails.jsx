import React, { useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { GlobalContext } from "../../../context/GlobalState";

import { fetchMovieDetails } from "../../../utils/Api";
import CardLoader from "../../helpers/loaders/cardLoader/CardLoader";
import {
  concatDirectorNames,
  concatGenreNames,
  concatWriterNames,
  getCoverImageURL,
} from "../../../utils/Util";
import DetailsTabs from "../../tabs/DetailsTabs";
import WatchlistButton from "../../watchlist/WatchlistButton";
import DeleteMediaButton from "../../media/DeleteMediaButton";

export default function MovieDetails() {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [loadingMovie, setLoadingMovie] = useState(true);
  const [errorMovie, setErrorMovie] = useState(null);

  const { sessionData } = useContext(GlobalContext);

  useEffect(() => {
    setLoadingMovie(true);
    fetchMovieDetails(id)
      .then((response) => {
        response.data.media_type = "movie";
        setMovie(response.data);
      })
      .catch((err) => {
        console.error(err);
        setErrorMovie(err);
      })
      .finally(() => {
        setLoadingMovie(false);
      });
  }, []);

  const DirectorsTab = ({ directorsAsText }) => {
    if (directorsAsText == null || directorsAsText === "") {
      return null;
    }
    return (
      <div className="border-t-2 border-onyx-tint">
        <div className="mt-2 mb-2 text-lg">
          <div className="">Directors: {directorsAsText}</div>
        </div>
      </div>
    );
  };
  const WritersTab = ({ writersAsText }) => {
    if (writersAsText == null || writersAsText === "") {
      return null;
    }
    return (
      <div className="border-y-2 border-onyx-tint">
        <div className="mt-2 mb-2 text-lg">
          <div className="">Writers: {writersAsText}</div>
        </div>
      </div>
    );
  };

  const MovieTabs = ({ directorsAsText, writersAsText }) => {
    if (
      (directorsAsText == null || directorsAsText === "") &&
      (writersAsText == null || writersAsText === "")
    ) {
      return null;
    }
    return (
      <div className=" mt-12">
        <DirectorsTab directorsAsText={directorsAsText} />
        <WritersTab writersAsText={writersAsText} />
      </div>
    );
  };

  //=======================================================================================
  if (loadingMovie) {
    return <CardLoader />;
  }
  if (movie === null || errorMovie != null) {
    return (
      <h2 className="px-4 mt-5 uppercase tracking-wider text-onyx-primary-30 text-lg font-bold">
        Unable to load movie details
      </h2>
    );
  }

  return (
    <>
      <div className="border-b border-onyx-tint">
        <div className="container mx-auto px-4 py-16 flex flex-col md:flex-row">
          <img
            className="w-64 md:w-96"
            src={getCoverImageURL(movie.cover_image_url)}
            alt="cover image"
          />
          <div className="md:ml-24">
            <h2 className="text-4xl font-semibold">{movie.title}</h2>
            <div className="flex flex-wrap items-center text-gray-400 text-sm mt-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                className="bi bi-star-fill fill-mellon-primary"
                viewBox="0 0 16 16"
              >
                <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
              </svg>
              <span className="ml-1">{movie.audience_rating}%</span>
              <span className="mx-2">|</span>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                className="bi bi-star-fill fill-blue-400"
                viewBox="0 0 16 16"
              >
                <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
              </svg>
              <span className="ml-1">
                {movie.critics_rating !== null ? movie.critics_rating : "N/A "}%
              </span>
              <span className="mx-2">|</span>
              <span>{movie.release_date}</span>
              <span className="mx-2">|</span>
              <span>{concatGenreNames(movie.genres, ", ")}</span>
            </div>
            <p className="text-onyx-contrast mt-8">{movie.description}</p>
            <MovieTabs
              directorsAsText={concatDirectorNames(movie.directors, ", ", 3)}
              writersAsText={concatWriterNames(movie.writers, ", ", 3)}
            />

            <div className="flex flex-row mt-12">
              <WatchlistButton
                visible={sessionData.isLoggedIn}
                media={movie}
                mediaType={"movie"}
              />
              <div className="ml-5">
                <DeleteMediaButton
                  visible={
                    sessionData.isLoggedIn &&
                    sessionData.user.role === "ADMINISTRATOR"
                  }
                  media={movie}
                  mediaType={"movie"}
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="border-b border-onyx-tint">
        <DetailsTabs
          id={movie.id}
          actors={movie.actors}
          critiques={movie.critiques}
        />
      </div>
    </>
  );
}
