import React, { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { deleteMovie, deleteShow } from "../../utils/Api";
import { useNavigate } from "react-router-dom";
import { GlobalContext } from "../../context/GlobalState";

export default function DeleteMediaButton({ visible, mediaType, media }) {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const { removeMovieFromWatchlist, removeShowFromWatchlist } =
    useContext(GlobalContext);

  function buttonName() {
    if (loading) {
      return "Loading...";
    }
    switch (mediaType) {
      case "movie":
        return "Delete movie";
      case "tv_show":
        return "Delete tv show";
      default:
        console.error("Unkown media type!");
        return "Unknown media type!";
    }
  }

  function buttonStyle() {
    return loading
      ? "bg-onyx-tint text-onyx-primary-10 hover:bg-onyx-primary-30"
      : "bg-red-900 text-white hover:bg-red-950";
  }
  function buttonAction() {
    switch (mediaType) {
      case "movie":
        setLoading(true);
        deleteMovie(media.id)
          .then((response) => {
            removeMovieFromWatchlist(media.id);
            navigate(`/`);
            toast.success(
              `Movie "${response.data.title}" (ID: ${media.id}) deleted`
            );
          })
          .catch((err) => {
            console.error(err);
            toast.error("Unable to delete movie");
          })
          .finally(() => {
            setLoading(false);
          });
        break;
      case "tv_show":
        setLoading(true);
        deleteShow(media.id)
          .then((response) => {
            removeShowFromWatchlist(media.id);
            navigate(`/`);
            toast.success(
              `TV show "${response.data.title}" (ID: ${media.id}) deleted`
            );
          })
          .catch((err) => {
            console.error(err);
            toast.error("Unable to delete tv show");
          })
          .finally(() => {
            setLoading(false);
          });
        break;
      default:
        console.error("Unkown media type!");
    }
  }

  //========================================================================
  if (!visible) {
    return null;
  }
  return (
    <button
      disabled={loading}
      onClick={buttonAction}
      className={
        "flex items-center rounded font-semibold px-5 py-4 transition ease-in-out duration-150 " +
        buttonStyle()
      }
    >
      <svg
        className="fill-white"
        width="25"
        height="25"
        viewBox="0 0 512 512"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path d="M199 103v50h-78v30h270v-30h-78v-50H199zm18 18h78v32h-78v-32zm-79.002 80l30.106 286h175.794l30.104-286H137.998zm62.338 13.38l.64 8.98 16 224 .643 8.976-17.956 1.283-.64-8.98-16-224-.643-8.976 17.956-1.283zm111.328 0l17.955 1.284-.643 8.977-16 224-.64 8.98-17.956-1.284.643-8.977 16-224 .64-8.98zM247 215h18v242h-18V215z" />
      </svg>
      <span className="ml-2">{buttonName()}</span>
    </button>
  );
}
