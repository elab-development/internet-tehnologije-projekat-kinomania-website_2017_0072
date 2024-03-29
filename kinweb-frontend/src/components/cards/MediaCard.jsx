import { Link } from "react-router-dom";
import { concatGenreNames, getCoverImageURL } from "../../utils/Util";

export default function MediaCard({ data }) {
  const detailsPageURL = getPageURL(data);

  function getPageURL(data) {
    switch (data.media_type) {
      case "movie":
        return `/movie/${data.id}`;
      case "tv_show":
        return `/show/${data.id}`;
      default:
        return "/";
    }
  }

  return (
    <div className="mt-8 px-2">
      <Link to={detailsPageURL}>
        <img
          className="h-[350px] hover:opacity-75 transition ease-in-out duration-150"
          src={getCoverImageURL(data.cover_image_url)}
          alt=""
        />
      </Link>
      <div className="mt-2">
        <Link to={detailsPageURL} className="text-lg mt-2 hover:text-gray:300">
          {data.title}
        </Link>
        <div className="flex items-center text-gray-400 text-sm mt-1">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
            className="bi bi-star-fill fill-mellon-primary"
            viewBox="0 0 16 16"
          >
            <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
          </svg>
          <span className="ml-1">{data.audience_rating}%</span>
          <span className="mx-2">|</span>
          <span>{data.release_date}</span>
        </div>
        <div className="text-gray-400 text-sm">
          {concatGenreNames(data.genres,", ")}
        </div>
      </div>
    </div>
  );
}
